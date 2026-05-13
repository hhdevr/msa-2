package com.chaykin.orderservice.schedule.task;

import com.chaykin.orderservice.persistence.model.async.AsyncMessage;
import com.chaykin.orderservice.schedule.processor.AsyncMessageSenderProcessor;
import com.chaykin.orderservice.service.AsyncMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class AsyncMessageSenderScheduledTask {

    private static final int BATCH_SIZE = 50;

    private final AsyncMessageService asyncMessageService;
    private final AsyncMessageSenderProcessor processor;

    @Scheduled(fixedDelay = 3000)
    public void sendOutboxMessages() {
        List<AsyncMessage> messages = asyncMessageService.getUnsentOutboxMessages(BATCH_SIZE);

        if (messages.isEmpty()) {
            return;
        }
        log.info("Outbox tick: picked up {} unsent message(s)", messages.size());

        for (AsyncMessage message: messages) {
            try {
                processor.sendMessage(message);
            } catch (Exception e) {
                log.error("Failed to send outbox message id={} topic={} — will retry on next tick",
                          message.getMessageId().getId(),
                          message.getMessageId().getTopic(),
                          e);
            }
        }
    }
}
