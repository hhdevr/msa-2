package com.chaykin.orderservice.converter;

import com.chaykin.orderservice.controller.model.CreateOrderItemRequest;
import com.chaykin.orderservice.controller.model.CreateOrderRequest;
import com.chaykin.orderservice.controller.model.UpdateOrderItemRequest;
import com.chaykin.orderservice.controller.model.UpdateOrderRequest;
import com.chaykin.orderservice.persistence.model.Order;
import com.chaykin.orderservice.persistence.model.OrderItem;
import com.chaykin.orderservice.service.model.OrderDto;
import com.chaykin.orderservice.service.model.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderConverter {

    OrderDto convert(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "active", ignore = true)
    Order convert(OrderDto dto);

    List<OrderDto> convert(List<Order> orders);

    OrderItemDto convert(OrderItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem convert(OrderItemDto dto);

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderDto convert(CreateOrderRequest request);

    OrderItemDto convert(CreateOrderItemRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderDto convert(UpdateOrderRequest request);

    OrderItemDto convert(UpdateOrderItemRequest request);
}
