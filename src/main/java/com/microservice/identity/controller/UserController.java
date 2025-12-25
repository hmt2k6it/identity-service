package com.microservice.identity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.identity.dto.request.UserCreationRequest;
import com.microservice.identity.dto.request.UserUpdateRequest;
import com.microservice.identity.entity.User;
import com.microservice.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping()
    public User createUser(@RequestBody UserCreationRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request)
            throws Exception {

        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
    }

    @GetMapping()
    public List<User> getUsers() {
        return userService.getUsers();
    }

}
