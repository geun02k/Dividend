package com.example.dividend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                                                    .code(e.getStatusCode())
                                                    .message(e.getMessage())
                                                    .build();
        // HttpStatus.resolve(e.getStatusCode()) 를 통해 HttpStatus에 상태코드를 담아서
        // errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                                    HttpStatus.resolve(e.getStatusCode()));
    }
}
