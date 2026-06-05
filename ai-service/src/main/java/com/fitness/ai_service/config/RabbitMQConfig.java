package com.fitness.ai_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue name from application.yml
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    // Exchange name from application.yml
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    // Routing key from application.yml
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    // Creates durable queue automatically in RabbitMQ
    @Bean
    public Queue activityQueue() {
        return QueueBuilder
                .durable(queueName)
                .build();
    }

    // Creates direct exchange automatically in RabbitMQ
    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange(exchangeName);
    }

    // Creates binding automatically:
    // activity-exchange + activity-created -> activity-queue
    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange) {
        return BindingBuilder
                .bind(activityQueue)
                .to(activityExchange)
                .with(routingKey);
    }

    // Converts Java object to JSON before sending to RabbitMQ
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}