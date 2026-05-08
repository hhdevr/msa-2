package com.chaykin.common.model.messaging;

import java.util.UUID;

public record PaymentResponseMessage(UUID orderRefId,
                                     UUID paymentGuid,
                                     boolean success,
                                     String failureReason) {

}
