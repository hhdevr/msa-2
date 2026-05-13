package com.chaykin.orderservice.persistence.model.async;

import com.chaykin.common.model.messaging.AsyncMessageStatus;
import com.chaykin.common.model.messaging.AsyncMessageType;
import com.chaykin.orderservice.persistence.model.common.PersistableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "async_messages")
@Getter
@Setter
@EqualsAndHashCode(of = "messageId", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsyncMessage extends PersistableEntity<AsyncMessageId> {

    @EmbeddedId
    private AsyncMessageId messageId;

    @Column(name = "headers")
    private String headers;

    @Column(name = "val", nullable = false, columnDefinition = "text")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AsyncMessageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AsyncMessageStatus status;

    @Override
    @Transient
    public AsyncMessageId getId() {
        return messageId;
    }
}
