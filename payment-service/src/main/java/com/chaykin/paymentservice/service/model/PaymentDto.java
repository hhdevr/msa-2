package com.chaykin.paymentservice.service.model;

import com.chaykin.paymentservice.persistence.model.PaymentMethod;
import com.chaykin.paymentservice.persistence.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentDto(
        UUID guid,
        UUID orderRefId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status,
        String transactionId,
        String note,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

}
