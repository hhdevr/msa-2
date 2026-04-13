package com.chaykin.common.model.order;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
        String productName,
        int quantity,
        BigDecimal price
) {

}
