package com.chaykin.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ServiceException extends RuntimeException {

    private final HttpStatus status;
    private final String message;
    private final int code;

    public ServiceException(ErrorCode error) {
        super(error.getMessage());
        this.status = error.getStatus();
        this.message = error.getMessage();
        this.code = error.getCode();
    }

    public ServiceException(ErrorCode error, UUID value) {
        super(String.format(error.getMessage(), value));
        this.status = error.getStatus();
        this.message = String.format(error.getMessage(), value);
        this.code = error.getCode();
    }

    public ServiceException(ErrorCode error, String value) {
        super(String.format(error.getMessage(), value));
        this.status = error.getStatus();
        this.message = String.format(error.getMessage(), value);
        this.code = error.getCode();
    }

    public ServiceException(ErrorCode error, long value) {
        super(String.format(error.getMessage(), value));
        this.status = error.getStatus();
        this.message = String.format(error.getMessage(), value);
        this.code = error.getCode();
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
