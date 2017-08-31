package com.keenvil.event.domain;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * RabbitTemplate wrapper to use only the default vhost.
 */
public class DefaultTemplate implements Template {

  private RabbitTemplate template;

  public DefaultTemplate(final ConnectionFactory connectionFactory) {
    template = new RabbitTemplate(connectionFactory);
    ExpressionParser parser = new SpelExpressionParser();
    Expression exp = parser.parseExpression("'default'");

    template.setSendConnectionFactorySelectorExpression(
        exp);

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
