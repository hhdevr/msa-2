package com.chaykin.deliveryservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@Entity
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID guid;

    @Column(nullable = false, name = "order_ref_id")
    private UUID orderRefId;

    @Column(nullable = false, name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Embedded
    private Address address;

    @Enumerated(STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

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
        Delivery delivery = (Delivery) o;
        return Objects.equals(guid, delivery.guid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(guid);
    }

    @Override
    public String toString() {
        return "Delivery{" +
               "id=" + id +
               ", guid=" + guid +
               ", orderRefId=" + orderRefId +
               ", recipientName='" + recipientName + '\'' +
               ", recipientPhone='" + recipientPhone + '\'' +
               ", address=" + address +
               ", status=" + status +
               ", active=" + active +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
