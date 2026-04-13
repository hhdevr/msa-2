package com.chaykin.common.model.payment;

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
