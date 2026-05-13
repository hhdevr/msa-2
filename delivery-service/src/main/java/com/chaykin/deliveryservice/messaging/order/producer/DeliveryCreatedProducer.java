package com.chaykin.deliveryservice.messaging.order.producer;

import com.chaykin.common.model.messaging.DeliveryCreatedMessage;
import com.chaykin.deliveryservice.messaging.order.config.properties.KafkaOrderServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DeliveryCreatedProducer {

    private final KafkaTemplate<String, DeliveryCreatedMessage> kafkaTemplate;
    private final KafkaOrderServiceProperties properties;

    public void send(DeliveryCreatedMessage message) {
        log.info("Sending delivery created message to Kafka topic={} for order {}, delivery {}",
                 properties.deliveryCreatedTopic(),
                 message.orderRefId(),
                 message.deliveryGuid());
        kafkaTemplate.send(properties.deliveryCreatedTopic(),
                           message.orderRefId().toString(),
                           message);
    }
}
