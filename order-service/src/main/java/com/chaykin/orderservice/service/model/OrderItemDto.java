package com.chaykin.orderservice.service.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        UUID guid,
        String productName,
        int quantity,
        BigDecimal price
) {

}
