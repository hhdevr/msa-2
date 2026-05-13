package com.chaykin.deliveryservice.messaging.order.listener;

import com.chaykin.common.model.delivery.DeliveryDto;
import com.chaykin.common.model.messaging.OrderPaidMessage;
import com.chaykin.deliveryservice.service.DeliveryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderPaidListener {

    private final DeliveryService deliveryService;

    @KafkaListener(topics = "${kafka.service.order.order-paid-topic}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, OrderPaidMessage> consumerRecord,
                        OrderPaidMessage message,
                        Acknowledgment ack) {
        log.info("Received order paid message for order {} (key={}, partition={}, offset={})",
                 message.orderRefId(),
                 consumerRecord.key(),
                 consumerRecord.partition(),
                 consumerRecord.offset());

        DeliveryDto delivery = deliveryService.createForPaidOrder(message.orderRefId());
        log.info("Delivery created for order {}: deliveryGuid={}, status={}",
                 message.orderRefId(),
                 delivery.guid(),
                 delivery.status());

        ack.acknowledge();
    }
}
