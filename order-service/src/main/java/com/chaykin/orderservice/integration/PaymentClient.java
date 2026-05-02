package com.chaykin.orderservice.integration;

import com.chaykin.common.exception.ExceptionMessageModel;
import com.chaykin.common.exception.ServiceException;
import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

import static com.chaykin.orderservice.exception.ErrorMessage.PAYMENT_REQUEST_FAILED;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentClient {

    private final PaymentFeignClient feignClient;
    private final ObjectMapper mapper;

    @RateLimiter(name = "paymentClientRL")
    @Bulkhead(name = "paymentClientBH")
    @Retry(name = "paymentServiceRetry")
    @CircuitBreaker(name = "paymentServiceCB")
    public PaymentDto createPayment(CreatePaymentRequest request) {
        try {
            log.info("Calling payment-service for order {}", request.orderRefId());
            return feignClient.createPayment(UUID.randomUUID(), request);
        } catch (FeignException ex) {
            processException(ex);
            throw new ServiceException(PAYMENT_REQUEST_FAILED, request.orderRefId());
        }
    }

    private void processException(FeignException ex) {
        HttpStatusCode statusCode = HttpStatusCode.valueOf(ex.status());
        Optional<ByteBuffer> byteBuffer = ex.responseBody();

        if (byteBuffer.isPresent()) {
            try {
                byte[] bytes = byteBuffer.get().array();
                ExceptionMessageModel error = mapper.readValue(bytes, ExceptionMessageModel.class);
                log.error("Payment service error [{}]: {}", statusCode, error.getMessage());
            } catch (Exception e) {
                log.error("Payment service error [{}]: unable to parse response", statusCode);
            }
        } else {
            log.error("Payment service error [{}]: no response body", statusCode);
        }
    }
}
