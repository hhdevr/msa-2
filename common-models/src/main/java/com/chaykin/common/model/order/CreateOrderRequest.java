package com.chaykin.common.model.order;

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
