package com.chaykin.orderservice.controller.model;

import com.chaykin.orderservice.persistence.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateOrderRequest(
        UUID guid,
        String customerName,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        String comment,
        List<UpdateOrderItemRequest> items
) {

}
