package com.chaykin.deliveryservice.controller;

import com.chaykin.common.model.delivery.CreateDeliveryRequest;
import com.chaykin.common.model.delivery.DeliveryDto;
import com.chaykin.common.model.delivery.UpdateDeliveryRequest;
import com.chaykin.deliveryservice.controller.docs.DeliveryApi;
import com.chaykin.deliveryservice.converter.DeliveryConverter;
import com.chaykin.deliveryservice.service.DeliveryService;
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
@RequestMapping(path = "/deliveries", produces = APPLICATION_JSON_VALUE)
public class DeliveryController implements DeliveryApi {

    private final DeliveryService service;
    private final DeliveryConverter converter;

    @Override
    @GetMapping
    @CircuitBreaker(name = "deliveryControllerCB", fallbackMethod = "findAllFallback")
    public ResponseEntity<List<DeliveryDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    @GetMapping("/{guid}")
    @CircuitBreaker(name = "deliveryControllerCB", fallbackMethod = "getByIdFallback")
    public ResponseEntity<DeliveryDto> getById(@PathVariable UUID guid) {
        log.info("GET delivery by id: {}", guid);
        return ResponseEntity.ok(service.getById(guid));
    }

    @Override
    @PostMapping
    @CircuitBreaker(name = "deliveryControllerCB", fallbackMethod = "createFallback")
    public ResponseEntity<DeliveryDto> create(@RequestBody CreateDeliveryRequest request) {
        DeliveryDto dto = converter.convert(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(service.create(dto));
    }

    @Override
    @PutMapping("/{guid}")
    @CircuitBreaker(name = "deliveryControllerCB", fallbackMethod = "updateFallback")
    public ResponseEntity<DeliveryDto> update(@PathVariable UUID guid,
                                              @RequestBody UpdateDeliveryRequest request) {
        DeliveryDto dto = converter.convert(request);
        return ResponseEntity.ok(service.update(dto));
    }

    @Override
    @DeleteMapping("/{guid}")
    @CircuitBreaker(name = "deliveryControllerCB", fallbackMethod = "deleteFallback")
    public ResponseEntity<Void> delete(@PathVariable UUID guid) {
        service.delete(guid);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<List<DeliveryDto>> findAllFallback(Throwable t) {
        log.warn("Circuit breaker open for findAll: {}", t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<DeliveryDto> getByIdFallback(UUID guid, Throwable t) {
        log.warn("Circuit breaker open for getById {}: {}", guid, t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<DeliveryDto> createFallback(CreateDeliveryRequest request, Throwable t) {
        log.warn("Circuit breaker open for create: {}", t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<DeliveryDto> updateFallback(UUID guid, UpdateDeliveryRequest request, Throwable t) {
        log.warn("Circuit breaker open for update: {}", t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<Void> deleteFallback(UUID guid, Throwable t) {
        log.warn("Circuit breaker open for delete {}: {}", guid, t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

}
