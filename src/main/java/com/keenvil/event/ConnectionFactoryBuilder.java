package com.keenvil.event;

import static org.slf4j.LoggerFactory.getLogger;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

public class ConnectionFactoryBuilder {
  
  private Map<String, String> properties = new HashMap<>();

  /** TLS hacia el broker (Amazon MQ lo exige). Default false: mismo comportamiento que antes. */
  private boolean ssl;

  private static Logger log = getLogger(ConnectionFactoryBuilder.class);
  
  public static ConnectionFactoryBuilder create() {
    return new ConnectionFactoryBuilder();
  }

  public ConnectionFactory build() {
    
    log.info("Building Connection Factory: {}, to host {}, port {}, vhost {}, ssl {}",
        properties.get("name"), properties.get("host"),
        properties.get("port"), properties.get("vhost"), ssl);

    CachingConnectionFactory connectionFactory =
        new CachingConnectionFactory();

    connectionFactory.setHost(properties.get("host"));
    connectionFactory.setPort(Integer.valueOf(properties.get("port")));
    connectionFactory.setVirtualHost(properties.get("vhost"));
    connectionFactory.setUsername(properties.get("username"));
    connectionFactory.setPassword(properties.get("password"));
    if (ssl) {
      try {
        // Truststore por defecto de la JVM (CAs publicas, como las de Amazon MQ) y verificacion del hostname.
        connectionFactory.getRabbitConnectionFactory().useSslProtocol(SSLContext.getDefault());
        connectionFactory.getRabbitConnectionFactory().enableHostnameVerification();
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("No se pudo habilitar TLS para RabbitMQ", e);
      }
    }
    return connectionFactory;
  }

  public ConnectionFactoryBuilder ssl(final boolean enabled) {
    this.ssl = enabled;
    return this;
  }

  /**
   * TLS de una conexion: la clave "ssl" del JSON de Consul (boolean o "true"/"false") manda;
   * si no esta, vale el default global (event.ssl.enabled).
   */
  static boolean sslFrom(final Object value, final boolean globalDefault) {
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    if (value != null && !String.valueOf(value).isBlank()) {
      return Boolean.parseBoolean(String.valueOf(value).trim());
    }
    return globalDefault;
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
