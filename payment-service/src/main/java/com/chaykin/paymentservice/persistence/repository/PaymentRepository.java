package com.chaykin.paymentservice.persistence.repository;

import com.chaykin.paymentservice.persistence.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByGuid(UUID guid);

    boolean existsByGuid(UUID guid);

    List<Payment> findAllByActiveTrue();
}
