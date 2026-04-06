package com.chaykin.common.model.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID guid,
        String customerName,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        String comment,
        List<OrderItemDto> items,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

}
