package com.chaykin.orderservice.messaging.common.listener;

import com.chaykin.common.model.messaging.AsyncMessageStatus;
import com.chaykin.common.model.messaging.AsyncMessageType;
import com.chaykin.orderservice.persistence.model.async.AsyncMessage;
import com.chaykin.orderservice.persistence.model.async.AsyncMessageId;
import com.chaykin.orderservice.service.AsyncMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.support.Acknowledgment;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public abstract class IdempotentKafkaListener<T> {

    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    private final AsyncMessageService asyncMessageService;
    private final JsonMapper jsonMapper;

    public void consume(ConsumerRecord<String, T> consumerRecord,
                        T message,
                        Acknowledgment acknowledgment) {
        Header idempotencyKeyHeader = consumerRecord.headers()
                                                    .lastHeader(IDEMPOTENCY_KEY_HEADER);
        if (idempotencyKeyHeader == null) {
            log.error("Idempotency key header is missing on consumer record from topic={} partition={} offset={}",
                      consumerRecord.topic(),
                      consumerRecord.partition(),
                      consumerRecord.offset());
            acknowledgment.acknowledge();
            return;
        }

        String idempotencyKey = new String(idempotencyKeyHeader.value(), StandardCharsets.UTF_8);

        AsyncMessageId messageId = new AsyncMessageId(idempotencyKey, consumerRecord.topic());
        AsyncMessage inboxRecord = AsyncMessage.builder()
                                               .messageId(messageId)
                                               .value(jsonMapper.writeValueAsString(message))
                                               .status(AsyncMessageStatus.RECEIVED)
                                               .type(AsyncMessageType.INBOX)
                                               .build();

        try {
            asyncMessageService.saveMessage(inboxRecord);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate message dropped: idempotencyKey={} topic={}",
                     idempotencyKey,
                     consumerRecord.topic());
            acknowledgment.acknowledge();
            return;
        }

        processConsumedMessage(message);
        acknowledgment.acknowledge();
    }

    public abstract void processConsumedMessage(T message);
}
