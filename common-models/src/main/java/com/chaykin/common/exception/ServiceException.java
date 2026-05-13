package com.chaykin.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ServiceException extends RuntimeException {

    private final HttpStatus status;
    private final String message;
    private final int code;

    public ServiceException(ErrorCode error) {
        this(error, (Throwable) null);
    }

    public ServiceException(ErrorCode error, UUID value) {
        this(error, value, null);
    }

    public ServiceException(ErrorCode error, String value) {
        this(error, value, null);
    }

    public ServiceException(ErrorCode error, long value) {
        this(error, value, null);
    }

    public ServiceException(ErrorCode error, Throwable cause) {
        super(error.getMessage(), cause);
        this.status = error.getStatus();
        this.message = error.getMessage();
        this.code = error.getCode();
    }

    public ServiceException(ErrorCode error, UUID value, Throwable cause) {
        super(String.format(error.getMessage(), value), cause);
        this.status = error.getStatus();
        this.message = String.format(error.getMessage(), value);
        this.code = error.getCode();
    }

    public ServiceException(ErrorCode error, String value, Throwable cause) {
        super(String.format(error.getMessage(), value), cause);
        this.status = error.getStatus();
        this.message = String.format(error.getMessage(), value);
        this.code = error.getCode();
    }

    public ServiceException(ErrorCode error, long value, Throwable cause) {
        super(String.format(error.getMessage(), value), cause);
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
