package com.chaykin.deliveryservice.controller;

import com.chaykin.deliveryservice.controller.model.CreateDeliveryRequest;
import com.chaykin.deliveryservice.controller.model.UpdateDeliveryRequest;
import com.chaykin.deliveryservice.converter.DeliveryConverter;
import com.chaykin.deliveryservice.service.DeliveryService;
import com.chaykin.deliveryservice.service.model.DeliveryDto;
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
@RequestMapping(path = "/deliveries",
                produces = APPLICATION_JSON_VALUE)
public class DeliveryController {

    private final DeliveryService service;

    private final DeliveryConverter converter;

    @GetMapping
    public ResponseEntity<List<DeliveryDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{guid}")
    public ResponseEntity<DeliveryDto> getById(@PathVariable UUID guid) {
        log.info("GET delivery by id: {}", guid);
        return ResponseEntity.ok(service.getById(guid));
    }

    @PostMapping
    public ResponseEntity<DeliveryDto> create(@RequestBody CreateDeliveryRequest request) {
        DeliveryDto dto = converter.convert(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(service.create(dto));
    }

    @PutMapping("/{guid}")
    public ResponseEntity<DeliveryDto> update(@RequestBody UpdateDeliveryRequest request) {
        DeliveryDto dto = converter.convert(request);
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping("/{guid}")
    public ResponseEntity<Void> delete(@PathVariable UUID guid) {
        service.delete(guid);
        return ResponseEntity.noContent().build();
    }
}
