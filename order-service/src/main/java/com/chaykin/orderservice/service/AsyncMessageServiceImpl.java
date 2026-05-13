package com.chaykin.orderservice.service;

import com.chaykin.common.model.messaging.AsyncMessageStatus;
import com.chaykin.orderservice.persistence.model.async.AsyncMessage;
import com.chaykin.orderservice.persistence.repository.AsyncMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AsyncMessageServiceImpl implements AsyncMessageService {

    private final AsyncMessageRepository repository;

    @Override
    @Transactional
    public void saveMessage(AsyncMessage message) {
        repository.save(message);
        log.debug("Saved outbox message id={} topic={} status={}",
                  message.getMessageId().getId(),
                  message.getMessageId().getTopic(),
                  message.getStatus());
    }

    @Override
    public List<AsyncMessage> getUnsentOutboxMessages(int batchSize) {
        Pageable pageable = PageRequest.of(0, batchSize);
        return repository.findUnsentOutboxMessages(pageable);
    }

    @Override
    @Transactional
    public void markAsSent(AsyncMessage message) {
        message.setStatus(AsyncMessageStatus.SENT);
        repository.save(message);
        log.debug("Marked outbox message id={} topic={} as SENT",
                  message.getMessageId().getId(), message.getMessageId().getTopic());
    }
}
