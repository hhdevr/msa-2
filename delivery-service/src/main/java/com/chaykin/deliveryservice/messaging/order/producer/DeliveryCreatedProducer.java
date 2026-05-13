package com.chaykin.deliveryservice.messaging.order.producer;

import com.chaykin.common.model.messaging.DeliveryCreatedMessage;
import com.chaykin.deliveryservice.messaging.order.config.properties.KafkaOrderServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class DeliveryCreatedProducer {

    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    private final KafkaTemplate<String, DeliveryCreatedMessage> kafkaTemplate;
    private final KafkaOrderServiceProperties properties;

    public void send(DeliveryCreatedMessage message) {
        String topic = properties.deliveryCreatedTopic();
        String key = message.orderRefId().toString();
        String idempotencyKey = UUID.randomUUID().toString();

        log.info("Sending delivery created message to topic={} order={} delivery={} idempotencyKey={}",
                 topic,
                 message.orderRefId(),
                 message.deliveryGuid(),
                 idempotencyKey);

        RecordHeader header = new RecordHeader(IDEMPOTENCY_KEY_HEADER, idempotencyKey.getBytes());
        ProducerRecord<String, DeliveryCreatedMessage> record = new ProducerRecord<>(topic,
                                                                                     null,
                                                                                     null,
                                                                                     key,
                                                                                     message,
                                                                                     List.of(header));

        kafkaTemplate.send(record);
    }
}
