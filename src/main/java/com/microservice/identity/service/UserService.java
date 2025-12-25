package com.microservice.identity.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.microservice.identity.dto.request.UserCreationRequest;
import com.microservice.identity.dto.request.UserUpdateRequest;
import com.microservice.identity.entity.User;
import com.microservice.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    public User createUser(UserCreationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());
        return userRepository.save(user);
    }

    public User updateUser(String userId, UserUpdateRequest request) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not existed!"));
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());
        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
