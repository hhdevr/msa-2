package com.chaykin.deliveryservice.controller.model;

import com.chaykin.deliveryservice.persistence.model.DeliveryStatus;
import com.chaykin.deliveryservice.service.model.AddressDto;

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
