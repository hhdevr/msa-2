package com.chaykin.orderservice.messaging.payment.listener;

import com.chaykin.common.model.messaging.PaymentResponseMessage;
import com.chaykin.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentResponseListener {

    private final OrderService orderService;

    @RabbitListener(queues = "${rabbitmq.service.payment.queue-response-name}")
    public void handle(PaymentResponseMessage message,
                       @Header(value = AmqpHeaders.CORRELATION_ID, required = false) String correlationId) {
        log.info("Received payment response for order {} (correlationId={}): success={}",
                 message.orderRefId(),
                 correlationId,
                 message.success());
        orderService.applyPaymentResult(message.orderRefId(), message.success());
    }
}
