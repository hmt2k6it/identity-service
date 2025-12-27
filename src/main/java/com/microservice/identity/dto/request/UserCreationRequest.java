package com.microservice.identity.dto.request;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Pattern(regexp = "^[a-zA-Z0-9]{3,}$", message = "USERNAME_INVALID")
    String username;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "INVALID_PASSWORD")
    String password;
    String firstName;
    String lastName;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "INVALID_EMAIL")
    String email;
    LocalDate dob;
    Set<String> roles;
}
