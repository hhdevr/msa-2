package com.chaykin.orderservice.persistence.repository;

import com.chaykin.orderservice.persistence.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByGuid(UUID guid);

    boolean existsByGuid(UUID guid);

    List<Order> findAllByActiveTrue();
}
