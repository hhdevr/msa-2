package com.chaykin.orderservice.exception;

import com.chaykin.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage implements ErrorCode {

    NULL_ID(HttpStatus.BAD_REQUEST, "ID cannot be null", 101),
    ORDER_NOT_EXIST(HttpStatus.NOT_FOUND, "Order id=%s does not exist", 102);

    private final HttpStatus status;
    private final String message;
    private final int code;
}
