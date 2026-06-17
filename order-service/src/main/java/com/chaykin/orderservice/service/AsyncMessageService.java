package com.chaykin.orderservice.service;

import com.chaykin.orderservice.persistence.model.async.AsyncMessage;

import java.util.List;

public interface AsyncMessageService {

    void saveMessage(AsyncMessage message);

    List<AsyncMessage> getUnsentOutboxMessages(int batchSize);

    void markAsSent(AsyncMessage message);
}
