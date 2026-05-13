package com.chaykin.orderservice.messaging.delivery.producer;

import com.chaykin.common.model.messaging.OrderPaidMessage;
import com.chaykin.orderservice.messaging.delivery.config.properties.KafkaDeliveryServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderPaidProducer {

    private final KafkaTemplate<String, OrderPaidMessage> kafkaTemplate;
    private final KafkaDeliveryServiceProperties properties;

    public void send(OrderPaidMessage message) {
        log.info("Sending order paid message to Kafka topic={} for order {}",
                 properties.orderPaidTopic(),
                 message.orderRefId());
        kafkaTemplate.send(properties.orderPaidTopic(),
                           message.orderRefId().toString(),
                           message);
    }
}
