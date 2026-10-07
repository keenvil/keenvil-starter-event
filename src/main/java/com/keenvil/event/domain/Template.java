package com.keenvil.event.domain;

import org.springframework.context.ApplicationContext;

/**
 * Template interface to send messages/events.
 */
public interface Template {

  void setExchange(final String exchange);

  void send(final String key, final Object message);

  /**
   * Turns on Micrometer observation (tracing + W3C propagation in the message headers) for the
   * underlying RabbitTemplate. No-op by default; see {@code event.observation.enabled}.
   */
  default void enableObservation(final ApplicationContext applicationContext) {
  }
}
