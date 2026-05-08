package com.chaykin.orderservice.messaging.payment.producer;

import com.chaykin.common.model.messaging.PaymentRequestMessage;
import com.chaykin.orderservice.messaging.payment.config.properties.RabbitMqPaymentServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentRequestProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqPaymentServiceProperties properties;

    public void send(PaymentRequestMessage message) {
        String correlationId = message.orderRefId().toString();
        MessagePostProcessor mpp = m -> {
            m.getMessageProperties().setCorrelationId(correlationId);
            return m;
        };

        log.info("Sending payment request to RabbitMQ for order {} (correlationId={})",
                 message.orderRefId(),
                 correlationId);
        rabbitTemplate.convertAndSend(
                properties.exchangeRequestName(),
                properties.queueRequestName(),
                message,
                mpp);
    }
}
