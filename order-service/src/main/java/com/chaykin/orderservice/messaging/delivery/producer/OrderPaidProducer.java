package com.chaykin.orderservice.messaging.delivery.producer;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.common.model.messaging.OrderPaidMessage;
import com.chaykin.orderservice.messaging.delivery.config.properties.KafkaDeliveryServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

import static com.chaykin.orderservice.exception.ErrorMessage.OUTBOX_PUBLISH_FAILED;

@Slf4j
@Component
@AllArgsConstructor
public class OrderPaidProducer {

    private final KafkaTemplate<String, OrderPaidMessage> kafkaTemplate;
    private final KafkaDeliveryServiceProperties properties;

    public void sendBlocking(OrderPaidMessage message) {
        String topic = properties.orderPaidTopic();
        String key = message.orderRefId().toString();
        log.info("Publishing order paid message to topic={} key={}", topic, key);

        try {
            kafkaTemplate.send(topic, key, message).get();
            log.debug("Broker acknowledged order paid message key={}", key);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ServiceException(OUTBOX_PUBLISH_FAILED,
                                       message.orderRefId(),
                                       ie);
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause() != null ? ee.getCause() : ee;
            throw new ServiceException(OUTBOX_PUBLISH_FAILED,
                                       message.orderRefId(),
                                       cause);
        }
    }
}
