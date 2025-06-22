package com.min.holidaykeeper.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}