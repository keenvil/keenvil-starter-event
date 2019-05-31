package com.keenvil.event;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

public class ConnectionFactoryBuilder {
  
  private Map<String, String> properties = new HashMap<>();

  private static Logger log = getLogger(ConnectionFactoryBuilder.class);
  
  public static ConnectionFactoryBuilder create() {
    return new ConnectionFactoryBuilder();
  }

  public ConnectionFactory build() {
    
    log.info("Building Connection Factory: {}, to host {}, port {}, vhost {}",
        properties.get("name"), properties.get("host"),
        properties.get("port"), properties.get("vhost"));

    CachingConnectionFactory connectionFactory =
        new CachingConnectionFactory();

    connectionFactory.setHost(properties.get("host"));
    connectionFactory.setPort(Integer.valueOf(properties.get("port")));
    connectionFactory.setVirtualHost(properties.get("vhost"));
    connectionFactory.setUsername(properties.get("username"));
    connectionFactory.setPassword(properties.get("password"));
    return connectionFactory;
  }
  
  public ConnectionFactoryBuilder host(final String host) {
    properties.put("host", host);
    return this;
  }
  
  public ConnectionFactoryBuilder port(final String port) {
    properties.put("port", port);
    return this;
  }
  
  public ConnectionFactoryBuilder vhost(final String vhost) {
    properties.put("vhost", vhost);
    return this;
  }
  
  public ConnectionFactoryBuilder username(final String username) {
    properties.put("username", username);
    return this;
  }
  
  public ConnectionFactoryBuilder password(final String password) {
    properties.put("password", password);
    return this;
  }

  public ConnectionFactoryBuilder name(final String name) {
    properties.put("name", name);
    return this;
  }
}
