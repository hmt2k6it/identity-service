package com.microservice.identity.configuration;

import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.microservice.identity.dto.request.IntrospectRequest;
import com.microservice.identity.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtCustomDecoder implements JwtDecoder {
    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder = null;
    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;
    AuthenticationService authenticationService;

    @Override
    public Jwt decode(String token) throws JwtException {
        if (!authenticationService.introspect(IntrospectRequest.builder()
                .token(token)
                .build()).isValid()) {
            throw new JwtException("Token not valid");
        }
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        try {
            return nimbusJwtDecoder.decode(token);
        } catch (Exception e) {
            throw new JwtException("Token not valid!");
        }
    }

}
