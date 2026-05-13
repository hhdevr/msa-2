package com.chaykin.orderservice.persistence.repository;

import com.chaykin.orderservice.persistence.model.async.AsyncMessage;
import com.chaykin.orderservice.persistence.model.async.AsyncMessageId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AsyncMessageRepository extends JpaRepository<AsyncMessage, AsyncMessageId> {

    @Query("SELECT m FROM AsyncMessage m " +
           "WHERE m.status = com.chaykin.common.model.messaging.AsyncMessageStatus.CREATED " +
           "  AND m.type = com.chaykin.common.model.messaging.AsyncMessageType.OUTBOX " +
           "ORDER BY m.createdAt")
    List<AsyncMessage> findUnsentOutboxMessages(Pageable pageable);
}
