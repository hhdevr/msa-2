package com.chaykin.paymentservice.service;

import com.chaykin.paymentservice.service.model.PaymentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    List<PaymentDto> findAll();

    Optional<PaymentDto> findById(UUID guid);

    PaymentDto getById(UUID guid);

    PaymentDto create(PaymentDto dto);

    PaymentDto update(PaymentDto dto);

    void delete(UUID guid);
}
