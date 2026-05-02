package com.chaykin.orderservice.integration;

import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentDto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentClient {

    private final PaymentFeignClient feignClient;

    @RateLimiter(name = "paymentClientRL")
    @Bulkhead(name = "paymentClientBH")
    @Retry(name = "paymentServiceRetry")
    @CircuitBreaker(name = "paymentServiceCB")
    public PaymentDto createPayment(UUID idempotencyKey, CreatePaymentRequest request) {
        log.info("Calling payment-service for order {}", request.orderRefId());
        return feignClient.createPayment(idempotencyKey, request);
    }

}
