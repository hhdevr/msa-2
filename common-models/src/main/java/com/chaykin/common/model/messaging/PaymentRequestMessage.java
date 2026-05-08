package com.chaykin.common.model.messaging;

import com.chaykin.common.model.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestMessage(UUID orderRefId,
                                    BigDecimal amount,
                                    String currency,
                                    PaymentMethod method) {

}
