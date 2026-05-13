package com.chaykin.deliveryservice.messaging.order.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.service.order")
public record KafkaOrderServiceProperties(String orderPaidTopic,
                                          String deliveryCreatedTopic) {

}
