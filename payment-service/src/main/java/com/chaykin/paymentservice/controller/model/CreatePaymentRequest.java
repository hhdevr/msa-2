package com.chaykin.paymentservice.controller.model;

import com.chaykin.paymentservice.persistence.model.PaymentMethod;
import com.chaykin.paymentservice.persistence.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        UUID orderRefId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status,
        String transactionId,
        String note
) {

}
