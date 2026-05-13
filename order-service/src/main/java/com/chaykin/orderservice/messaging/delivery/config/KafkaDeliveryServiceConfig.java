package com.chaykin.orderservice.messaging.delivery.config;

import com.chaykin.orderservice.messaging.delivery.config.properties.KafkaDeliveryServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KafkaDeliveryServiceProperties.class)
public class KafkaDeliveryServiceConfig {

}
