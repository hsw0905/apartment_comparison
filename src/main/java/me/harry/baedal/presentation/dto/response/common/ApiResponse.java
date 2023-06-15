package me.harry.baedal.presentation.dto.response.common;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private final Type type;
    private final LocalDateTime timestamp;
    private final int statusCode;
    private final T body;

    private ApiResponse(Type type, int statusCode, T body) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.body = body;
    }

    public static <T> ApiResponse<T> of(Type type, int statusCode, T body) {
        return new ApiResponse<>(type, statusCode, body);
    }

}
