package com.chaykin.deliveryservice.service.model;

import com.chaykin.deliveryservice.persistence.model.DeliveryStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeliveryDto(
        UUID guid,
        UUID orderRefId,
        String recipientName,
        String recipientPhone,
        AddressDto address,
        DeliveryStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

}
