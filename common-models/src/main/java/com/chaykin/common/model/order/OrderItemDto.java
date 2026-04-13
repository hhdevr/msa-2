package com.chaykin.common.model.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        UUID guid,
        String productName,
        int quantity,
        BigDecimal price
) {

}
