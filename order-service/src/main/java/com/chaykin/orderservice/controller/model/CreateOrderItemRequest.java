package com.chaykin.orderservice.controller.model;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
        String productName,
        int quantity,
        BigDecimal price
) {

}
