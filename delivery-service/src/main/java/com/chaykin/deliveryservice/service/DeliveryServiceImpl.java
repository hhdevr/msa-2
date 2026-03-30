package com.chaykin.deliveryservice.service;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.deliveryservice.converter.DeliveryConverter;
import com.chaykin.deliveryservice.persistence.model.Delivery;
import com.chaykin.deliveryservice.persistence.repository.DeliveryRepository;
import com.chaykin.deliveryservice.service.model.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.chaykin.deliveryservice.exception.ErrorMessage.DELIVERY_NOT_EXIST;

@Slf4j
@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;
    private final DeliveryConverter converter;

    @Override
    public List<DeliveryDto> findAll() {
        return repository.findAllByActiveTrue()
                         .stream()
                         .map(converter::convert)
                         .toList();
    }

    @Override
    public Optional<DeliveryDto> findById(UUID guid) {
        return repository.findByGuid(guid)
                         .filter(Delivery::isActive)
                         .map(converter::convert);
    }

    @Override
    public DeliveryDto getById(UUID guid) {
        return converter.convert(repository.findByGuid(guid)
                                           .filter(Delivery::isActive)
                                           .orElseThrow(() -> {
                                               log.error("Delivery not found with id {}", guid);
                                               return new ServiceException(DELIVERY_NOT_EXIST, guid);
                                           }));
    }

    @Override
    public DeliveryDto create(DeliveryDto dto) {
        var entity = converter.convert(dto);
        entity.setActive(true);
        var saved = repository.save(entity);
        return converter.convert(saved);
    }

    @Override
    public DeliveryDto update(DeliveryDto dto) {
        Delivery existing = repository.findByGuid(dto.guid())
                                      .filter(Delivery::isActive)
                                      .orElseThrow(() -> new ServiceException(DELIVERY_NOT_EXIST, dto.guid()));
        Delivery entity = converter.convert(dto);
        entity.setId(existing.getId());
        entity.setActive(true);
        return converter.convert(repository.save(entity));
    }

    @Override
    public void delete(UUID guid) {
        Delivery entity = repository.findByGuid(guid)
                                    .filter(Delivery::isActive)
                                    .orElseThrow(() -> {
                                        log.error("Delivery with id {} could not be deleted", guid);
                                        return new ServiceException(DELIVERY_NOT_EXIST, guid);
                                    });
        entity.setActive(false);
        repository.save(entity);
    }
}
