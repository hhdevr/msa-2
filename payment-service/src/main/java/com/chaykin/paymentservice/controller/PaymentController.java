package com.chaykin.paymentservice.controller;

import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentDto;
import com.chaykin.common.model.payment.UpdatePaymentRequest;
import com.chaykin.paymentservice.controller.docs.PaymentApi;
import com.chaykin.paymentservice.converter.PaymentConverter;
import com.chaykin.paymentservice.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/payments", produces = APPLICATION_JSON_VALUE)
public class PaymentController implements PaymentApi {

    private final PaymentService service;
    private final PaymentConverter converter;

    @Override
    @GetMapping
    @CircuitBreaker(name = "paymentControllerCB", fallbackMethod = "findAllFallback")
    public ResponseEntity<List<PaymentDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    @GetMapping("/{guid}")
    @CircuitBreaker(name = "paymentControllerCB", fallbackMethod = "getByIdFallback")
    public ResponseEntity<PaymentDto> getById(@PathVariable UUID guid) {
        log.info("GET payment by id: {}", guid);
        return ResponseEntity.ok(service.getById(guid));
    }

    @Override
    @PostMapping
    @CircuitBreaker(name = "paymentControllerCB", fallbackMethod = "createFallback")
    public ResponseEntity<PaymentDto> create(@RequestBody CreatePaymentRequest request) {
        PaymentDto dto = converter.convert(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(service.create(dto));
    }

    @Override
    @PutMapping("/{guid}")
    @CircuitBreaker(name = "paymentControllerCB", fallbackMethod = "updateFallback")
    public ResponseEntity<PaymentDto> update(@PathVariable UUID guid,
                                             @RequestBody UpdatePaymentRequest request) {
        PaymentDto dto = converter.convert(request);
        return ResponseEntity.ok(service.update(dto));
    }

    @Override
    @DeleteMapping("/{guid}")
    @CircuitBreaker(name = "paymentControllerCB", fallbackMethod = "deleteFallback")
    public ResponseEntity<Void> delete(@PathVariable UUID guid) {
        service.delete(guid);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<List<PaymentDto>> findAllFallback(Throwable t) {
        log.warn("Circuit breaker open for findAll: {}", t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<PaymentDto> getByIdFallback(UUID guid, Throwable t) {
        log.warn("Circuit breaker open for getById {}: {}", guid, t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<PaymentDto> createFallback(CreatePaymentRequest request, Throwable t) {
        log.warn("Circuit breaker open for create: {}", t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<PaymentDto> updateFallback(UUID guid, UpdatePaymentRequest request, Throwable t) {
        log.warn("Circuit breaker open for update: {}", t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<Void> deleteFallback(UUID guid, Throwable t) {
        log.warn("Circuit breaker open for delete {}: {}", guid, t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
