package com.chaykin.paymentservice.messaging.order.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.service.order")
public record RabbitMqOrderServiceProperties(String exchangeRequestName,
                                             String queueRequestName,
                                             String exchangeResponseName,
                                             String queueResponseName,
                                             String dlxName,
                                             String dlqName) {

}
