package com.chaykin.deliveryservice.persistence.repository;

import com.chaykin.deliveryservice.persistence.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByGuid(UUID guid);

    Optional<Delivery> findByOrderRefId(UUID orderRefId);

    boolean existsByGuid(UUID guid);

    boolean existsByOrderRefId(UUID orderRefId);

    List<Delivery> findAllByActiveTrue();
}
