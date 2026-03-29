package com.chaykin.paymentservice.persistence.model;

import jakarta.persistence.Column;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID guid;

    @Column(nullable = false, name = "order_ref_id")
    private UUID orderRefId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(columnDefinition = "text")
    private String note;

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
        Payment payment = (Payment) o;
        return Objects.equals(guid, payment.guid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(guid);
    }

    @Override
    public String toString() {
        return "Payment{" +
               "id=" + id +
               ", guid=" + guid +
               ", orderRefId=" + orderRefId +
               ", amount=" + amount +
               ", currency='" + currency + '\'' +
               ", method=" + method +
               ", status=" + status +
               ", transactionId='" + transactionId + '\'' +
               ", note='" + note + '\'' +
               ", active=" + active +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
