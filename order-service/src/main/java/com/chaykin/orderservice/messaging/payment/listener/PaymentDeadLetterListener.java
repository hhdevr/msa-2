package com.chaykin.orderservice.messaging.payment.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentDeadLetterListener {

    @RabbitListener(queues = "${rabbitmq.service.payment.dlq-name}")
    public void handle(Message message,
                       @Header(value = AmqpHeaders.CORRELATION_ID, required = false) String correlationId) {
        log.error("Payment response moved to DLQ (correlationId={}): body={}",
                  correlationId,
                  new String(message.getBody()));
    }
}
