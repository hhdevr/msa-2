package com.chaykin.deliveryservice.messaging.order.config;

import com.chaykin.deliveryservice.messaging.order.config.properties.KafkaOrderServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KafkaOrderServiceProperties.class)
public class KafkaOrderServiceConfig {

}
