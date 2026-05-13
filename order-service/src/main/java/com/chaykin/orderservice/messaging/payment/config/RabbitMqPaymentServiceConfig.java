package com.chaykin.orderservice.messaging.payment.config;

import com.chaykin.orderservice.messaging.payment.config.properties.RabbitMqPaymentServiceProperties;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(RabbitMqPaymentServiceProperties.class)
public class RabbitMqPaymentServiceConfig {

    private final RabbitMqPaymentServiceProperties properties;

    @Bean
    public Queue responseQueue() {
        return QueueBuilder.durable(properties.queueResponseName())
                           .withArgument("x-dead-letter-exchange", properties.dlxName())
                           .withArgument("x-dead-letter-routing-key", properties.dlqName())
                           .build();
    }

    @Bean
    public DirectExchange responseExchange() {
        return new DirectExchange(properties.exchangeResponseName());
    }

    @Bean
    public Binding responseBinding(Queue responseQueue,
                                   DirectExchange responseExchange) {
        return BindingBuilder.bind(responseQueue)
                             .to(responseExchange)
                             .with(properties.queueResponseName());
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(properties.dlqName()).build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(properties.dlxName());
    }

    @Bean
    public Binding dlqBinding(Queue dlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlq).to(dlxExchange).with(properties.dlqName());
    }

    @Bean
    public DirectExchange requestExchange() {
        return new DirectExchange(properties.exchangeRequestName());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf,
                                                                               MessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
