package com.chaykin.common.model.delivery;

import java.util.UUID;

public record UpdateDeliveryRequest(
        UUID guid,
        UUID orderRefId,
        String recipientName,
        String recipientPhone,
        AddressDto address,
        DeliveryStatus status
) {

}
