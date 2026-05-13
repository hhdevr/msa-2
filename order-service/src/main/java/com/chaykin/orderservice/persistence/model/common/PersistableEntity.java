package com.chaykin.orderservice.persistence.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;

@Getter
@MappedSuperclass
public abstract class PersistableEntity<T> implements Persistable<T> {

    @Column(name = "created_at", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
