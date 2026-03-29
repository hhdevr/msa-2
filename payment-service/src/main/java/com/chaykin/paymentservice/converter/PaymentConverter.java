package com.chaykin.paymentservice.converter;

import com.chaykin.paymentservice.controller.model.CreatePaymentRequest;
import com.chaykin.paymentservice.controller.model.UpdatePaymentRequest;
import com.chaykin.paymentservice.persistence.model.Payment;
import com.chaykin.paymentservice.service.model.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentConverter {

    PaymentDto convert(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Payment convert(PaymentDto dto);

    List<PaymentDto> convert(List<Payment> payments);

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentDto convert(CreatePaymentRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentDto convert(UpdatePaymentRequest request);
}
