package com.microservice.identity.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.identity.dto.request.AuthenticationRequest;
import com.microservice.identity.dto.request.IntrospectRequest;
import com.microservice.identity.dto.request.LogoutRequest;
import com.microservice.identity.dto.request.RefreshTokenRequest;
import com.microservice.identity.dto.response.AuthenticationResponse;
import com.microservice.identity.dto.response.IntrospectResponse;
import com.microservice.identity.entity.Permission;
import com.microservice.identity.entity.RefreshToken;
import com.microservice.identity.entity.Role;
import com.microservice.identity.entity.User;
import com.microservice.identity.exception.AppException;
import com.microservice.identity.exception.ErrorCode;
import com.microservice.identity.repository.RefreshTokenRepository;
import com.microservice.identity.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RefreshTokenRepository refreshTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    long REFRESHABLE_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String accessToken = generateToken(user, false);
        String refreshToken = generateToken(user, true);
        saveRefreshToken(refreshToken);
        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(noRollbackFor = AppException.class)
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verify(request.getRefreshToken());
        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        Optional<RefreshToken> existingToken = refreshTokenRepository.findById(jit);
        if (existingToken.isEmpty()) {
            refreshTokenRepository.deleteAllByUserId(userId);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        refreshTokenRepository.delete(existingToken.get());
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        String newAccessToken = generateToken(user, false);
        String newRefreshToken = generateToken(user, true);
        saveRefreshToken(newRefreshToken);
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .authenticated(true)
                .build();
    }

    public void logout(LogoutRequest request) {
        try {
            SignedJWT signedJWT = verify(request.getRefreshToken());
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            refreshTokenRepository.deleteById(jit);
        } catch (Exception e) {
            log.info("Token already invalid or expired");
            return;
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            verify(request.getToken());
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private String generateToken(User user, boolean isRefreshToken) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        long duration = isRefreshToken ? REFRESHABLE_DURATION : VALID_DURATION;
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(user.getUserId())
                .issuer("microservice")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(duration, ChronoUnit.MINUTES).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString());
        if (!isRefreshToken) {
            claimsBuilder.claim("scope", buildScope(user.getRoles()));
        }
        Payload payload = new Payload(claimsBuilder.build().toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    private String buildScope(Set<Role> roles) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!Objects.isNull(roles)) {
            roles.forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                Set<Permission> permissions = role.getPermissions();
                if (!Objects.isNull(permissions)) {
                    permissions.forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }

    private SignedJWT verify(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        boolean isValid = signedJWT.verify(verifier);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!(isValid && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private void saveRefreshToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            RefreshToken refreshToken = RefreshToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .userId(userId)
                    .build();
            refreshTokenRepository.save(refreshToken);
        } catch (ParseException e) {
            log.error("Cannot parse token to save");
        }
    }
}
