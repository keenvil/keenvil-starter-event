package com.keenvil.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

public class ConnectionFactoryBuilderTest {

  private CachingConnectionFactory build(final boolean ssl) {
    return (CachingConnectionFactory) ConnectionFactoryBuilder.create()
        .name("default").host("rabbitmq.local").port("5671").vhost("/default")
        .username("u").password("p").ssl(ssl).build();
  }

  @Test
  public void sinSslQuedaComoAntes() {
    CachingConnectionFactory cf = build(false);
    assertFalse(cf.getRabbitConnectionFactory().isSSL());
    assertEquals("rabbitmq.local", cf.getHost());
    assertEquals(5671, cf.getPort());
    assertEquals("/default", cf.getVirtualHost());
  }

  @Test
  public void conSslUsaTls() {
    assertTrue(build(true).getRabbitConnectionFactory().isSSL());
  }

  @Test
  public void sslDelJsonDeConsulPisaElDefaultGlobal() {
    assertTrue(ConnectionFactoryBuilder.sslFrom(Boolean.TRUE, false));
    assertFalse(ConnectionFactoryBuilder.sslFrom(Boolean.FALSE, true));
    assertTrue(ConnectionFactoryBuilder.sslFrom("true", false));
    assertFalse(ConnectionFactoryBuilder.sslFrom(" false ", true));
  }

  @Test
  public void sinClaveSslValeElDefaultGlobal() {
    assertFalse(ConnectionFactoryBuilder.sslFrom(null, false));
    assertTrue(ConnectionFactoryBuilder.sslFrom(null, true));
    assertTrue(ConnectionFactoryBuilder.sslFrom("", true));
  }
}
