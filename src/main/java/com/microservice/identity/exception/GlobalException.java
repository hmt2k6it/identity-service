package com.microservice.identity.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.microservice.identity.dto.response.ApiResponse;

import jakarta.validation.ConstraintViolation;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingException(Exception exception) {
        ApiResponse<String> apiResonse = new ApiResponse<>();
        apiResonse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResonse.setMessage(exception.getMessage());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResonse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException appException) {
        ApiResponse<String> apiResonse = new ApiResponse<>();
        apiResonse.setCode(appException.getErrorCode().getCode());
        apiResonse.setMessage(appException.getErrorCode().getMessage());
        return ResponseEntity.status(appException.getErrorCode().getStatusCode()).body(apiResonse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        var fieldError = exception.getFieldError();
        String enumKey = fieldError.getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Object minValue = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
        }

        var constraintViolation = fieldError.unwrap(ConstraintViolation.class);
        var attributes = constraintViolation.getConstraintDescriptor().getAttributes();
        minValue = attributes.get("min");

        String message = errorCode.getMessage();
        if (minValue != null) {
            message = message.replace("{min}", String.valueOf(minValue));
        }

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(message);

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
