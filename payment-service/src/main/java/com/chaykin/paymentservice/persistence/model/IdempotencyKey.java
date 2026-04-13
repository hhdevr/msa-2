package com.chaykin.paymentservice.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

import static com.chaykin.paymentservice.persistence.model.IdempotencyStatus.PENDING;
import static jakarta.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyKey {

    @Id
    private UUID key;

    @Enumerated(STRING)
    private IdempotencyStatus idempotencyStatus;

    @Lob
    private String response;

    private int statusCode;

    public IdempotencyKey(UUID key) {
        this.key = key;
        this.idempotencyStatus = PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdempotencyKey that = (IdempotencyKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

}
