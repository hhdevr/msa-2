package com.chaykin.orderservice.messaging.delivery.listener;

import com.chaykin.common.model.messaging.DeliveryCreatedMessage;
import com.chaykin.orderservice.messaging.common.listener.IdempotentKafkaListener;
import com.chaykin.orderservice.service.AsyncMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class DeliveryCreatedListener extends IdempotentKafkaListener<DeliveryCreatedMessage> {

    public DeliveryCreatedListener(AsyncMessageService asyncMessageService, JsonMapper jsonMapper) {
        super(asyncMessageService, jsonMapper);
    }

    @KafkaListener(topics = "${kafka.service.delivery.delivery-created-topic}",
                   groupId = "${spring.kafka.consumer.group-id}",
                   properties = {
                           "spring.json.value.default.type=com.chaykin.common.model.messaging.DeliveryCreatedMessage"
                   })
    @Override
    public void consume(ConsumerRecord<String, DeliveryCreatedMessage> consumerRecord,
                        DeliveryCreatedMessage message,
                        Acknowledgment acknowledgment) {
        super.consume(consumerRecord, message, acknowledgment);
    }

    @Override
    public void processConsumedMessage(DeliveryCreatedMessage message) {
        log.info("Consumed delivery.created event: orderRefId={} deliveryGuid={}",
                 message.orderRefId(),
                 message.deliveryGuid());
    }
}
