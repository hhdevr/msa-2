package com.chaykin.paymentservice.messaging.order.listener;

import com.chaykin.common.model.messaging.PaymentRequestMessage;
import com.chaykin.common.model.messaging.PaymentResponseMessage;
import com.chaykin.common.model.payment.PaymentStatus;
import com.chaykin.paymentservice.messaging.order.producer.PaymentResponseProducer;
import com.chaykin.paymentservice.persistence.model.Payment;
import com.chaykin.paymentservice.persistence.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentRequestListener {

    private final PaymentRepository repository;
    private final PaymentResponseProducer responseProducer;

    @Transactional
    @RabbitListener(queues = "${rabbitmq.service.order.queue-request-name}")
    public void handle(PaymentRequestMessage request,
                       @Header(value = AmqpHeaders.CORRELATION_ID, required = false) String correlationId) {
        log.info("Received payment request for order {} (correlationId={})",
                 request.orderRefId(),
                 correlationId);

        var existing = repository.findByOrderRefId(request.orderRefId());
        if (existing.isPresent()) {
            Payment payment = existing.get();
            log.warn("Duplicate payment request for order {} — replaying response",
                     request.orderRefId());
            boolean success = payment.getStatus() == PaymentStatus.COMPLETED;
            responseProducer.send(new PaymentResponseMessage(payment.getOrderRefId(),
                                                             payment.getGuid(),
                                                             success,
                                                             null),
                                  correlationId);
            return;
        }

        Payment payment = new Payment();
        payment.setOrderRefId(request.orderRefId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setMethod(request.method());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setActive(true);
        Payment saved = repository.save(payment);

        log.info("Created payment {} for order {}",
                 saved.getGuid(),
                 saved.getOrderRefId());
        responseProducer.send(new PaymentResponseMessage(saved.getOrderRefId(),
                                                         saved.getGuid(),
                                                         true,
                                                         null),
                              correlationId);
    }
}
