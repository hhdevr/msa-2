package com.chaykin.orderservice.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.chaykin.orderservice.integration")
public class FeignConfig {

}
