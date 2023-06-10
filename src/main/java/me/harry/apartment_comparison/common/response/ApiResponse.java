package me.harry.apartment_comparison.common.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private final Type type;
    private final LocalDateTime timestamp;
    private final T body;

    private ApiResponse(Type type, T body) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.body = body;
    }

    public static <T> ApiResponse<T> of(Type type, T body) {
        return new ApiResponse<>(type, body);
    }

}
