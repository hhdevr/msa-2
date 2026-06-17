package com.chaykin.orderservice.messaging.delivery.listener;

import com.chaykin.common.model.messaging.DeliveryCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeliveryCreatedListener {

    @KafkaListener(topics = "${kafka.service.delivery.delivery-created-topic}",
                   groupId = "${spring.kafka.consumer.group-id}",
                   properties = {
                           "spring.json.value.default.type=com.chaykin.common.model.messaging.DeliveryCreatedMessage"
                   })
    public void consume(ConsumerRecord<String, DeliveryCreatedMessage> consumerRecord,
                        DeliveryCreatedMessage message,
                        Acknowledgment ack) {
        log.info("Received delivery created message: orderRefId={}, deliveryGuid={} (key={}, partition={}, offset={})",
                 message.orderRefId(),
                 message.deliveryGuid(),
                 consumerRecord.key(),
                 consumerRecord.partition(),
                 consumerRecord.offset());

        ack.acknowledge();
    }
}
