package com.chaykin.paymentservice.persistence.repository;

import com.chaykin.paymentservice.persistence.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, UUID> {

    @Lock(PESSIMISTIC_WRITE)
    Optional<IdempotencyKey> findByKey(UUID key);

}
