package com.chaykin.paymentservice.service;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.paymentservice.persistence.model.IdempotencyKey;
import com.chaykin.paymentservice.persistence.repository.IdempotencyKeyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.chaykin.paymentservice.exception.ErrorMessage.IDEMPOTENCY_KEY_EXISTS;
import static com.chaykin.paymentservice.exception.ErrorMessage.IDEMPOTENCY_KEY_NOT_FOUND;
import static com.chaykin.paymentservice.persistence.model.IdempotencyStatus.COMPLETED;

@Slf4j
@Service
@AllArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyKeyRepository repository;

    @Transactional
    @Override
    public void createPendingKey(UUID key) {
        IdempotencyKey newKey = new IdempotencyKey(key);

        try {
            repository.save(newKey);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(IDEMPOTENCY_KEY_EXISTS, key);
        }

    }

    @Override
    public Optional<IdempotencyKey> findByKeyLocked(UUID key) {
        return repository.findByKey(key);
    }

    @Transactional
    @Override
    public void markAsCompleted(UUID key, String responseData, int statusCode) {
        IdempotencyKey keyEntity = findByKeyLocked(key).orElseThrow(
                () -> new ServiceException(IDEMPOTENCY_KEY_NOT_FOUND, key)
        );
        keyEntity.setIdempotencyStatus(COMPLETED);
        keyEntity.setStatusCode(statusCode);
        keyEntity.setResponse(responseData);
        repository.save(keyEntity);
    }

}
