package com.keenvil.event.domain;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import com.keenvil.event.CommunityBasedRabbitConnectionFactory;
import com.keenvil.event.DefaultRabbitConnectionFactory;

/**
 * RabbitTemplate wrapper to use only the default vhost.
 */
public class DefaultTemplate implements Template {

  private RabbitTemplate template;

  private static Logger log = getLogger(
      DefaultTemplate.class);
  

  public DefaultTemplate(final DefaultRabbitConnectionFactory connectionFactory) {
    log.info("Creating Default Template");

    template = new RabbitTemplate(connectionFactory);
    ExpressionParser parser = new SpelExpressionParser();
    Expression exp = parser.parseExpression("'default'");

    template.setSendConnectionFactorySelectorExpression(
        exp);
    
    log.info("Default Template Created");

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
    log.info("Sending Message: {} to Key: {} from Default Templatte", message, key);
    template.convertAndSend(key, message);
  }
}
