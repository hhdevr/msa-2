package com.chaykin.orderservice.controller.model;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateOrderItemRequest(
        UUID guid,
        String productName,
        int quantity,
        BigDecimal price
) {

}
