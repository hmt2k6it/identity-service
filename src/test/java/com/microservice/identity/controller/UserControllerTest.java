package com.microservice.identity.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.microservice.identity.dto.request.UserCreationRequest;
import com.microservice.identity.dto.response.UserResponse;
import com.microservice.identity.service.UserService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreationRequest request;
    private UserResponse userResponse;

    @BeforeEach
    void initData() {
        request = UserCreationRequest.builder()
                .username("hmt2k62006")
                .password("Hmt2k2006!")
                .email("thaydoi070@gmail.com")
                .build();

        userResponse = UserResponse.builder()
                .username("hmt2k62006")
                .email("thaydoi070@gmail.com")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        String content = objectMapper.writeValueAsString(request);
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000));
    }

}