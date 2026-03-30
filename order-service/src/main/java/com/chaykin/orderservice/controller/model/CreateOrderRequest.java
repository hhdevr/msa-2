package com.chaykin.orderservice.controller.model;

import com.chaykin.orderservice.persistence.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        String customerName,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        String comment,
        List<CreateOrderItemRequest> items
) {

}
