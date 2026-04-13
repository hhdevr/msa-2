package com.chaykin.orderservice.service;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.common.model.order.OrderDto;
import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentMethod;
import com.chaykin.common.model.payment.PaymentStatus;
import com.chaykin.orderservice.converter.OrderConverter;
import com.chaykin.orderservice.integration.PaymentClient;
import com.chaykin.orderservice.persistence.model.Order;
import com.chaykin.orderservice.persistence.model.OrderItem;
import com.chaykin.orderservice.persistence.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.chaykin.orderservice.exception.ErrorMessage.ORDER_NOT_EXIST;

@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderConverter converter;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> findAll() {
        return repository.findAllByActiveTrue()
                         .stream()
                         .map(converter::convert)
                         .toList();
    }

    @Override
    public Optional<OrderDto> findById(UUID guid) {
        return repository.findByGuid(guid)
                         .filter(Order::isActive)
                         .map(converter::convert);
    }

    @Override
    public OrderDto getById(UUID guid) {
        return converter.convert(repository.findByGuid(guid)
                                           .filter(Order::isActive)
                                           .orElseThrow(() -> {
                                               log.error("Order not found with id {}", guid);
                                               return new ServiceException(ORDER_NOT_EXIST, guid);
                                           }));
    }

    @Override
    public OrderDto create(OrderDto dto) {
        Order entity = converter.convert(dto);
        entity.setActive(true);
        if (dto.items() != null) {
            List<OrderItem> items = dto.items()
                                       .stream()
                                       .map(converter::convert)
                                       .toList();
            entity.getItems().addAll(items);
            entity.getItems().forEach(item -> item.setOrder(entity));
        }
        Order saved = repository.save(entity);

        paymentClient.createPayment(new CreatePaymentRequest(
                saved.getGuid(),
                saved.getTotalAmount(),
                saved.getCurrency(),
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.PENDING,
                null,
                null
        ));

        return converter.convert(saved);
    }

    @Override
    public OrderDto update(OrderDto dto) {
        Order existing = repository.findByGuid(dto.guid())
                                   .filter(Order::isActive)
                                   .orElseThrow(() -> new ServiceException(ORDER_NOT_EXIST, dto.guid()));
        Order entity = converter.convert(dto);
        entity.setId(existing.getId());
        entity.setActive(true);
        if (dto.items() != null) {
            List<OrderItem> items = dto.items().stream()
                                       .map(converter::convert)
                                       .toList();
            entity.getItems().addAll(items);
            entity.getItems().forEach(item -> item.setOrder(entity));
        }
        return converter.convert(repository.save(entity));
    }

    @Override
    public void delete(UUID guid) {
        Order entity = repository.findByGuid(guid)
                                 .filter(Order::isActive)
                                 .orElseThrow(() -> {
                                     log.error("Order with id {} could not be deleted", guid);
                                     return new ServiceException(ORDER_NOT_EXIST, guid);
                                 });
        entity.setActive(false);
        repository.save(entity);
    }
}
