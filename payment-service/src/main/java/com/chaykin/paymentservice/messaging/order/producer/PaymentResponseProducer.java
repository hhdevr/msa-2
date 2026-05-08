package com.chaykin.paymentservice.messaging.order.producer;

import com.chaykin.common.model.messaging.PaymentResponseMessage;
import com.chaykin.paymentservice.messaging.order.config.properties.RabbitMqOrderServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentResponseProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqOrderServiceProperties properties;

    public void send(PaymentResponseMessage message, String correlationId) {
        MessagePostProcessor mpp = m -> {
            if (correlationId != null) {
                m.getMessageProperties().setCorrelationId(correlationId);
            }
            return m;
        };

        log.info("Sending payment response for order {} (correlationId={}, success={})",
                 message.orderRefId(),
                 correlationId,
                 message.success());
        rabbitTemplate.convertAndSend(properties.exchangeResponseName(),
                                      properties.queueResponseName(),
                                      message,
                                      mpp);
    }
}
