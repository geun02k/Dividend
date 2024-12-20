package com.example.dividend.exception;

import lombok.Builder;
import lombok.Data;

// 에러발생시 응답을 위한 모델
// 모든 에러에 대해 일괄적인 응답을 하기위해 사용.
@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
}
