package com.chaykin.paymentservice.service;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.paymentservice.converter.PaymentConverter;
import com.chaykin.paymentservice.persistence.model.Payment;
import com.chaykin.paymentservice.persistence.repository.PaymentRepository;
import com.chaykin.paymentservice.service.model.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.chaykin.paymentservice.exception.ErrorMessage.PAYMENT_NOT_EXIST;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentConverter converter;

    @Override
    public List<PaymentDto> findAll() {
        return repository.findAllByActiveTrue()
                         .stream()
                         .map(converter::convert)
                         .toList();
    }

    @Override
    public Optional<PaymentDto> findById(UUID guid) {
        return repository.findByGuid(guid)
                         .filter(Payment::isActive)
                         .map(converter::convert);
    }

    @Override
    public PaymentDto getById(UUID guid) {
        return converter.convert(repository.findByGuid(guid)
                                           .filter(Payment::isActive)
                                           .orElseThrow(() -> {
                                               log.error("Payment not found with id {}", guid);
                                               return new ServiceException(PAYMENT_NOT_EXIST, guid);
                                           }));
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Payment entity = converter.convert(dto);
        entity.setActive(true);
        Payment saved = repository.save(entity);
        return converter.convert(saved);
    }

    @Override
    public PaymentDto update(PaymentDto dto) {
        Payment existing = repository.findByGuid(dto.guid())
                                     .filter(Payment::isActive)
                                     .orElseThrow(() -> new ServiceException(PAYMENT_NOT_EXIST, dto.guid()));
        Payment entity = converter.convert(dto);
        entity.setId(existing.getId());
        entity.setActive(true);
        return converter.convert(repository.save(entity));
    }

    @Override
    public void delete(UUID guid) {
        Payment entity = repository.findByGuid(guid)
                                   .filter(Payment::isActive)
                                   .orElseThrow(() -> {
                                       log.error("Payment with id {} could not be deleted", guid);
                                       return new ServiceException(PAYMENT_NOT_EXIST, guid);
                                   });
        entity.setActive(false);
        repository.save(entity);
    }
}
