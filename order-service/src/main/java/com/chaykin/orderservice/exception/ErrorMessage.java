package com.chaykin.orderservice.exception;

import com.chaykin.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage implements ErrorCode {

    NULL_ID(HttpStatus.BAD_REQUEST, "ID cannot be null", 101),

    ORDER_NOT_EXIST(HttpStatus.NOT_FOUND, "Order id=%s does not exist", 102),

    PAYMENT_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "Payment request failed for order id=%s", 201),

    OUTBOX_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Failed to serialize outbox payload for order id=%s",
                                301),

    OUTBOX_DESERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
                                  "Failed to deserialize outbox message id=%s",
                                  302),

    OUTBOX_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
                          "Failed to publish outbox message to Kafka for order id=%s",
                          303);

    private final HttpStatus status;
    private final String message;
    private final int code;
}
