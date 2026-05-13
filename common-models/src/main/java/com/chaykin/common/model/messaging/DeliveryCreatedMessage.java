package com.chaykin.common.model.messaging;

import java.util.UUID;

public record DeliveryCreatedMessage(UUID orderRefId, UUID deliveryGuid) {

}
