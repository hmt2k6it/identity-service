package com.microservice.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.microservice.identity.dto.request.UserCreationRequest;
import com.microservice.identity.dto.request.UserUpdateRequest;
import com.microservice.identity.dto.response.UserResponse;
import com.microservice.identity.exception.AppException;
import com.microservice.identity.exception.ErrorCode;
import com.microservice.identity.exception.GlobalException;
import com.microservice.identity.service.UserService;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;

    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new GlobalException()).build();
        userCreationRequest = UserCreationRequest.builder()
                .username("hmt2k62006")
                .password("Hmt2k62006!")
                .email("thaydoi070@gmail.com")
                .build();
        userResponse = UserResponse.builder()
                .userId("user-id")
                .username("hmt2k62006")
                .email("thaydoi070@gmail.com")
                .status(true)
                .deleted(false)
                .build();
        userUpdateRequest = UserUpdateRequest.builder()
                .password("Nlnq28062007!")
                .email("nlnq28062007@gmail.com")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        String content = objectMapper.writeValueAsString(userCreationRequest);
        when(userService.createUser(any())).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.username").value("hmt2k62006"));
        verify(userService).createUser(userCreationRequest);
    }

    @Test
    void createUser_inValidUsername_fail() throws Exception {
        userCreationRequest.setUsername("ab");
        String content = objectMapper.writeValueAsString(userCreationRequest);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1003))
                .andExpect(jsonPath("message").value(
                        "Username must be at least 3 characters and contain only letters and numbers"));
    }

    @Test
    void createUser_inValidPassword_fail() throws Exception {
        userCreationRequest.setPassword("ab");
        String content = objectMapper.writeValueAsString(userCreationRequest);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1004))
                .andExpect(jsonPath("message").value(
                        "Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter, one number and one special character"));
    }

    @Test
    void createUser_userExisted_fail() throws Exception {
        String content = objectMapper.writeValueAsString(userCreationRequest);
        when(userService.createUser(any())).thenThrow(new AppException(ErrorCode.USER_EXISTED));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1002))
                .andExpect(jsonPath("message").value("User already existed"));
        verify(userService).createUser(userCreationRequest);
    }

    @Test
    void deleteUser_validId_success() throws Exception {
        String userId = "user-id";
        when(userService.deleteUser(userId)).thenReturn("User has been deleted successfully!");
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value("User has been deleted successfully!"));
        verify(userService).deleteUser(userId);
    }

    @Test
    void getUser_validId_success() throws Exception {
        String userId = "user-id";
        when(userService.getUser(userId)).thenReturn(userResponse);
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.userId").value(userId));
        verify(userService).getUser(userId);
    }

    @Test
    void getAllUser_success() throws Exception {
        List<UserResponse> list = List.of(userResponse);
        when(userService.getUsers()).thenReturn(list);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").isArray())
                .andExpect(jsonPath("result[0].username").value(userResponse.getUsername()));
        verify(userService).getUsers();
    }

    @Test
    void getAllUsers_emptyList_success() throws Exception {
        when(userService.getUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").isArray())
                .andExpect(jsonPath("result.length()").value(0));
    }

    @Test
    void updateUser_validRequest_success() throws Exception {
        userResponse.setEmail("nlnq28062007@gmail.com");
        String content = objectMapper.writeValueAsString(userUpdateRequest);
        String userId = "user-id";
        when(userService.updateUser(eq(userId), any())).thenReturn(userResponse);
        mockMvc.perform(put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.userId").value(userId))
                .andExpect(jsonPath("result.email").value(userResponse.getEmail()));
        verify(userService).updateUser(userId, userUpdateRequest);
    }

    @Test
    void getMyinfo_success() throws Exception {
        String userId = userResponse.getUserId();
        when(userService.getMyInfo()).thenReturn(userResponse);
        mockMvc.perform(get("/users/myinfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.userId").value(userId));
        verify(userService).getMyInfo();
    }

}
