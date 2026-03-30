package com.chaykin.paymentservice.exception;

import com.chaykin.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage implements ErrorCode {

    NULL_ID(HttpStatus.BAD_REQUEST, "ID cannot be null", 101),
    PAYMENT_NOT_EXIST(HttpStatus.NOT_FOUND, "Payment id=%s does not exist", 102);

    private final HttpStatus status;
    private final String message;
    private final int code;
}
