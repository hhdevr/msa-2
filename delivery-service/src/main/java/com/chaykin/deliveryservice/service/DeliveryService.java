package com.chaykin.deliveryservice.service;

import com.chaykin.common.model.delivery.DeliveryDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryService {

    List<DeliveryDto> findAll();

    Optional<DeliveryDto> findById(UUID guid);

    DeliveryDto getById(UUID guid);

    DeliveryDto create(DeliveryDto dto);

    DeliveryDto update(DeliveryDto dto);

    void delete(UUID guid);
}
