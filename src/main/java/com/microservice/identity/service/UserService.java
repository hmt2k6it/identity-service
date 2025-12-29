package com.microservice.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservice.identity.dto.request.UserCreationRequest;
import com.microservice.identity.dto.request.UserUpdateRequest;
import com.microservice.identity.dto.response.UserResponse;
import com.microservice.identity.entity.Role;
import com.microservice.identity.entity.User;
import com.microservice.identity.exception.AppException;
import com.microservice.identity.exception.ErrorCode;
import com.microservice.identity.mapper.UserMapper;
import com.microservice.identity.repository.RoleRepository;
import com.microservice.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    @NonFinal
    @Value("${app.security.default-role}")
    String DEFAULT_ROLE;
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findById(DEFAULT_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasAuthority('GET_USER')")
    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasAuthority('DELETE_USER')")
    public String deleteUser(String userId) {
        userRepository.deleteById(userId);
        return "User has been deleted!";
    }

    @PreAuthorize("hasAuthority('GET_ALLUSER')")
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toListUserResponse(users);
    }
}
