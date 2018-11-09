package com.keenvil.event.domain;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * RabbitTemplate wrapper to use for community based queues.
 */
public class CommunityBasedTemplate implements Template {

  private RabbitTemplate template;

  public CommunityBasedTemplate(final ConnectionFactory connectionFactory) {
    template = new RabbitTemplate(connectionFactory);
  }

  public void setMessageConverter(final MessageConverter messageConverter) {
    template.setMessageConverter(messageConverter);
  }

  @Override
  public void setExchange(final String exchange) {
    template.setExchange(exchange);
  }

  @Override
  public void send(String key, Object message) {
    template.convertAndSend(key, message);
  }
}
