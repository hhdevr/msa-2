package com.chaykin.common.model.order;

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
