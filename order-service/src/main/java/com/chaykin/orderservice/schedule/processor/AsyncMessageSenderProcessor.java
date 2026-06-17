package com.chaykin.orderservice.schedule.processor;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.common.model.messaging.OrderPaidMessage;
import com.chaykin.orderservice.messaging.delivery.producer.OrderPaidProducer;
import com.chaykin.orderservice.persistence.model.async.AsyncMessage;
import com.chaykin.orderservice.service.AsyncMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import static com.chaykin.orderservice.exception.ErrorMessage.OUTBOX_DESERIALIZATION_FAILED;

@Slf4j
@Component
@AllArgsConstructor
public class AsyncMessageSenderProcessor {

    private final AsyncMessageService asyncMessageService;
    private final OrderPaidProducer orderPaidProducer;
    private final JsonMapper jsonMapper;

    @Transactional
    public void sendMessage(AsyncMessage message) {
        OrderPaidMessage payload;
        try {
            payload = jsonMapper.readValue(message.getValue(), OrderPaidMessage.class);
        } catch (Exception e) {
            throw new ServiceException(OUTBOX_DESERIALIZATION_FAILED,
                                       message.getMessageId().getId(),
                                       e);
        }

        orderPaidProducer.sendBlocking(payload);

        asyncMessageService.markAsSent(message);
        log.info("Outbox message id={} sent to topic={} and marked SENT",
                 message.getMessageId().getId(),
                 message.getMessageId().getTopic());
    }
}
