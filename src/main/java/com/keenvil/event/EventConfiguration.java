package com.keenvil.event;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.keenvil.event.domain.DefaultTemplate;

/**
 * Event module configuration.
 * 
 * <p>Event module handles common configuration and utility classes to work
 * with events inside Keenvil Application.</p>
 */
@Configuration
public class EventConfiguration {

  private static Logger log = getLogger(
      EventConfiguration.class);

  @Value("${event.listener.maxConcurrentConsumers:10}")
  private Integer maxConcurrentConsumers;

  @Value("${event.listener.concurrentConsumers:3}")
  private Integer concurrentConsumers;

  @Bean
  @ConditionalOnMissingBean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
    SimpleRabbitListenerContainerFactory factory =
        new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setConcurrentConsumers(concurrentConsumers);
    factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }

  // Tipo de retorno declarado como CommunityBasedRabbitConnectionFactory (no
  // la interfaz ConnectionFactory): Spring registra el bean bajo el tipo
  // DECLARADO del metodo @Bean, no el tipo runtime del objeto devuelto.
  // Consumidores que inyectan por el tipo concreto (p.ej. communityTemplate
  // en CrowdEventConfig/GuardEventConfig) necesitan que el bean quede
  // registrado bajo ese tipo especifico; con ConnectionFactory como tipo
  // declarado, esa inyeccion siempre fallaba con "No qualifying bean" --
  // enmascarado en produccion hasta ahora porque ninguna app migrada a
  // Boot 3 habia llegado tan lejos en el arranque (bugs previos de cork).
  // Sigue siendo asignable a ConnectionFactory para quien inyecte por ahi.
  @Bean
  @ConditionalOnMissingBean
  public CommunityBasedRabbitConnectionFactory connectionFactory() {
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
  public DefaultRabbitConnectionFactory defaultConnectionFactory() {
    Map<Object, ConnectionFactory> connectionFactories =
        new HashMap<Object, ConnectionFactory>();
    
    properties().getEventHosts()
        .stream()
        .filter(tc -> tc.isDefault())
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

    log.info("Default Connection Factory: {}",
        connectionFactories.get(properties().getDefaultHost().getName()));
    
    DefaultRabbitConnectionFactory connectionFactory =
        new DefaultRabbitConnectionFactory();

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
