package com.chaykin.common.model.order;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateOrderItemRequest(
        UUID guid,
        String productName,
        int quantity,
        BigDecimal price
) {

}
