package com.chaykin.orderservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID guid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, name = "product_name")
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @PrePersist
    public void prePersist() {
        if (guid == null) {
            guid = UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(guid, orderItem.guid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(guid);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
               "id=" + id +
               ", guid=" + guid +
               ", productName='" + productName + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               '}';
    }
}
