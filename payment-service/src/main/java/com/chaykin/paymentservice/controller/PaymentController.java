package com.chaykin.paymentservice.controller;

import com.chaykin.paymentservice.controller.model.CreatePaymentRequest;
import com.chaykin.paymentservice.controller.model.UpdatePaymentRequest;
import com.chaykin.paymentservice.converter.PaymentConverter;
import com.chaykin.paymentservice.service.PaymentService;
import com.chaykin.paymentservice.service.model.PaymentDto;
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
@RequestMapping(path = "/payments",
                produces = APPLICATION_JSON_VALUE)
public class PaymentController {

    private final PaymentService service;

    private final PaymentConverter converter;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{guid}")
    public ResponseEntity<PaymentDto> getById(@PathVariable UUID guid) {
        log.info("GET payment by id: {}", guid);
        return ResponseEntity.ok(service.getById(guid));
    }

    @PostMapping
    public ResponseEntity<PaymentDto> create(@RequestBody CreatePaymentRequest request) {
        PaymentDto dto = converter.convert(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(service.create(dto));
    }

    @PutMapping("/{guid}")
    public ResponseEntity<PaymentDto> update(@RequestBody UpdatePaymentRequest request) {
        PaymentDto dto = converter.convert(request);
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping("/{guid}")
    public ResponseEntity<Void> delete(@PathVariable UUID guid) {
        service.delete(guid);
        return ResponseEntity.noContent().build();
    }
}
