package com.chaykin.orderservice.integration;

import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

import static com.chaykin.common.model.payment.PaymentHeaders.KEY_HEADER;

@FeignClient(name = "payment-service", url = "${integration.payment-service.url}")
public interface PaymentFeignClient {

    @PostMapping("/payments")
    PaymentDto createPayment(@RequestHeader(KEY_HEADER) UUID idempotencyKey,
                             @RequestBody CreatePaymentRequest request);
}
