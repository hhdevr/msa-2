package com.chaykin.orderservice.messaging.delivery.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.service.delivery")
public record KafkaDeliveryServiceProperties(String orderPaidTopic,
                                             String deliveryCreatedTopic) {

}
