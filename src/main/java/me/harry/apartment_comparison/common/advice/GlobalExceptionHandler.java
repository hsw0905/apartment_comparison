package me.harry.apartment_comparison.common.advice;

import lombok.extern.slf4j.Slf4j;
import me.harry.apartment_comparison.common.exception.BadRequestException;
import me.harry.apartment_comparison.common.response.ApiResponse;
import me.harry.apartment_comparison.common.response.ErrorResponse;
import me.harry.apartment_comparison.common.response.Type;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBindException(BindingResult bindingResult) {
        String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();

        log.error(errorMessage);

        return ResponseEntity.badRequest().body(
                ApiResponse.of(Type.FAILURE, new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                        errorMessage))
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage());

        return ResponseEntity.badRequest().body(
                ApiResponse.of(Type.FAILURE, new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage()))
        );
    }
}
