package com.microservice.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // 1. Hệ thống & Chưa xác định
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    TOKEN_CREATION_FAILED(1010, "Failed to create security token", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_WARNING(1012, "Hacker", HttpStatus.INTERNAL_SERVER_ERROR),

    // 2. Xác thực & Phân quyền (Security)
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),

    // 3. Dữ liệu đầu vào (Validation & Regex)
    USER_EXISTED(1002, "User already existed", HttpStatus.BAD_REQUEST),

    // Cập nhật thông báo cho Username Regex
    USERNAME_INVALID(1003, "Username must be at least 3 characters and contain only letters and numbers",
            HttpStatus.BAD_REQUEST),

    // Cập nhật thông báo cho Password Regex (Khớp với các quy tắc đặc biệt)
    INVALID_PASSWORD(1004,
            "Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter, one number and one special character",
            HttpStatus.BAD_REQUEST),

    // Cập nhật thông báo cho Email Regex
    INVALID_EMAIL(1005, "Email format is invalid (e.g., user@example.com)", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1011, "Your age must be at least {min}!", HttpStatus.BAD_REQUEST),

    // 4. Truy vấn dữ liệu (Entity)
    USER_NOT_EXISTED(1008, "User not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1009, "Role not existed", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}