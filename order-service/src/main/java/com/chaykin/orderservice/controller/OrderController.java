package com.chaykin.orderservice.controller;

import com.chaykin.common.model.order.CreateOrderRequest;
import com.chaykin.common.model.order.OrderDto;
import com.chaykin.common.model.order.UpdateOrderRequest;
import com.chaykin.orderservice.controller.docs.OrderApi;
import com.chaykin.orderservice.converter.OrderConverter;
import com.chaykin.orderservice.service.OrderService;
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
@RequestMapping(path = "/orders", produces = APPLICATION_JSON_VALUE)
public class OrderController implements OrderApi {

    private final OrderService service;
    private final OrderConverter converter;

    @Override
    @GetMapping
    public ResponseEntity<List<OrderDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    @GetMapping("/{guid}")
    public ResponseEntity<OrderDto> getById(@PathVariable UUID guid) {
        log.info("GET order by id: {}", guid);
        return ResponseEntity.ok(service.getById(guid));
    }

    @Override
    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestBody CreateOrderRequest request) {
        OrderDto dto = converter.convert(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(service.create(dto));
    }

    @Override
    @PutMapping("/{guid}")
    public ResponseEntity<OrderDto> update(@RequestBody UpdateOrderRequest request) {
        OrderDto dto = converter.convert(request);
        return ResponseEntity.ok(service.update(dto));
    }

    @Override
    @DeleteMapping("/{guid}")
    public ResponseEntity<Void> delete(@PathVariable UUID guid) {
        service.delete(guid);
        return ResponseEntity.noContent().build();
    }
}
