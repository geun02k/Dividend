package com.example.dividend.exception;

public abstract class AbstractException extends RuntimeException {
    // 에러상태코드 반환
    abstract public int getStatusCode();
    // 에러코드에 대한 에러메시지 반환
    abstract public String getMessage();
}
