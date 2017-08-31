package com.keenvil.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  
  
  @Value("${event.listener.maxConcurrentConsumers}")
  private Integer maxConcurrentConsumers;

  @Bean
  @ConditionalOnMissingBean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
    SimpleRabbitListenerContainerFactory factory =
        new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }

  @Bean
  @ConditionalOnMissingBean
  public ConnectionFactory connectionFactory() {
    Map<Object, ConnectionFactory> connectionFactories =
        new HashMap<Object, ConnectionFactory>();
    
    properties().getEventHosts()
        .stream()
        .forEach(tc -> connectionFactories.put(tc.getName(),
          ConnectionFactoryBuilder.create()
              .name(tc.getName())
              .host(tc.getHost())
              .port(String.valueOf(tc.getPort()))
              .vhost(tc.getVhost())
              .username(tc.getUsername())
              .password(tc.getPassword())
              .build()
            ));
    
    CommunityBasedRabbitConnectionFactory connectionFactory =
        new CommunityBasedRabbitConnectionFactory();
    
    connectionFactory.setTargetConnectionFactories(connectionFactories);
    connectionFactory.setDefaultTargetConnectionFactory(
        connectionFactories.get(properties().getDefaultHost().getName()));
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
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  @ConditionalOnMissingBean
  public EventProperties properties() {
    return new EventProperties();
  }

}
