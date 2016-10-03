package com.keenvil.event.domain;

/**
 * Template interface to send messages/events.
 */
public interface Template {

  void setExchange(final String exchange);

  void send(final String key, final Object message);
}
