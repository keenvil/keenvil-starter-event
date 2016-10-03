package com.keenvil.event;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Event module configuration.
 * 
 * <p>Event module handles common configuration and utility classes to work
 * with events inside Keenvil Application.</p>
 */
@Configuration
public class EventConfiguration {


  @Bean
  @ConditionalOnMissingBean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
    SimpleRabbitListenerContainerFactory factory =
        new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setMaxConcurrentConsumers(properties().getMaxConcurrentConsumers());
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }

  @Bean
  @ConditionalOnMissingBean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory =
        new CachingConnectionFactory();
    connectionFactory.setHost(properties().getHost());
    connectionFactory.setPort(properties().getPort());
    connectionFactory.setVirtualHost(properties().getVhost());
    connectionFactory.setUsername(properties().getUsername());
    connectionFactory.setPassword(properties().getPassword());
    return connectionFactory;
  }

  @Bean
  @ConditionalOnMissingBean
  public AmqpAdmin amqpAdmin() {
    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
    return rabbitAdmin;
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageConverter jsonMessageConverter() {
    return new JsonMessageConverter();
  }

  @Bean
  @ConditionalOnMissingBean
  public EventProperties properties() {
    return new EventProperties();
  }

}
