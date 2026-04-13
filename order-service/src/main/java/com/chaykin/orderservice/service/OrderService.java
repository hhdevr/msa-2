package com.chaykin.orderservice.service;

import com.chaykin.common.model.order.OrderDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> findAll();

    Optional<OrderDto> findById(UUID guid);

    OrderDto getById(UUID guid);

    OrderDto create(OrderDto dto);

    OrderDto update(OrderDto dto);

    void delete(UUID guid);
}
