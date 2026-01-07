package com.microservice.identity.controller;

import com.microservice.identity.dto.request.UserCreationRequest;
import com.microservice.identity.dto.request.UserUpdateRequest;
import com.microservice.identity.dto.response.UserResponse;
import com.microservice.identity.service.UserService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private UserUpdateRequest userUpdateRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void initData() {
        // Chuẩn bị dữ liệu mẫu chuẩn Regex đã định nghĩa trong DTO
        userCreationRequest = UserCreationRequest.builder()
                .username("hmt2k62006") // > 3 ký tự
                .password("Hmt2k62006!") // Thỏa mãn regex password phức tạp
                .email("thaydoi070@gmail.com") // Đúng format email
                .build();

        userResponse = UserResponse.builder()
                .username("hmt2k62006")
                .email("thaydoi070@gmail.com")
                .status(true)
                .build();

        userUpdateRequest = UserUpdateRequest.builder()
                .password("NewPassword123!")
                .email("newemail@example.com")
                .build();
    }

    // --- TEST CREATE USER ---

    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // Mocking: Khi gọi service.createUser thì trả về userResponse
        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(userResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("hmt2k62006"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.email").value("thaydoi070@gmail.com"));
    }

    @Test
    void createUser_invalidUsername_fail() throws Exception {
        // GIVEN
        userCreationRequest.setUsername("ab"); // Ngắn hơn 3 ký tự -> Invalid
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Username must be at least 3 characters and contain only letters and numbers"));
        // Mong đợi 400 Bad Request do vi phạm @Valid
    }

    // --- TEST GET USER ---

    @Test
    void getUser_validId_success() throws Exception {
        // GIVEN
        String userId = "user-id-uuid";
        Mockito.when(userService.getUser(userId)).thenReturn(userResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("hmt2k62006"));
    }

    // --- TEST UPDATE USER ---

    @Test
    void updateUser_validRequest_success() throws Exception {
        // GIVEN
        String userId = "user-id-uuid";
        String content = objectMapper.writeValueAsString(userUpdateRequest);

        // Giả lập kết quả trả về sau khi update
        UserResponse updatedResponse = UserResponse.builder()
                .username("hmt2k62006")
                .email("new.email@example.com")
                .status(true)
                .build();

        Mockito.when(userService.updateUser(ArgumentMatchers.eq(userId), ArgumentMatchers.any()))
                .thenReturn(updatedResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.email").value("new.email@example.com"));
    }

    // --- TEST DELETE USER ---

    @Test
    void deleteUser_validId_success() throws Exception {
        // GIVEN
        String userId = "user-id-uuid";
        Mockito.when(userService.deleteUser(userId)).thenReturn("User has been deleted!");

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("User has been deleted!"));
    }

    // --- TEST GET ALL USERS ---

    @Test
    void getUsers_success() throws Exception {
        // GIVEN
        Mockito.when(userService.getUsers()).thenReturn(List.of(userResponse));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].username").value("hmt2k62006"));
    }

    // --- TEST GET MY INFO ---
    @Test
    void getMyInfo_success() throws Exception {
        // GIVEN
        Mockito.when(userService.getMyInfo()).thenReturn(userResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/myinfo")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("hmt2k62006"));
    }
}