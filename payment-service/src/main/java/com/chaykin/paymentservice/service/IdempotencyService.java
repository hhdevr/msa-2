package com.chaykin.paymentservice.service;

import com.chaykin.paymentservice.persistence.model.IdempotencyKey;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyService {

    void createPendingKey(UUID key);

    Optional<IdempotencyKey> findByKeyLocked(UUID key);

    void markAsCompleted(UUID key, String responseData, int statusCode);

}
