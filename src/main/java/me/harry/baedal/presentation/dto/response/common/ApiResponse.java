package me.harry.baedal.presentation.dto.response.common;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private final Type type;
    private final LocalDateTime timestamp;
    private final int statusCode;
    private final T data;

    private ApiResponse(Type type, int statusCode, T data) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(Type type, int statusCode, T data) {
        return new ApiResponse<>(type, statusCode, data);
    }

}
