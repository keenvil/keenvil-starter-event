package com.keenvil.event;

import com.keenvil.cork.consul.ConsulService;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.AbstractRoutingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultRabbitConnectionFactory
    extends AbstractRoutingConnectionFactory {


  @Autowired(required = false)
  ConsulService consulService;

  @Value("${event.ssl.enabled:false}")
  boolean sslEnabled;

  // Credenciales del host por defecto: se usan cuando el JSON del tenant en Consul no trae usuario/password.
  private String fallbackUsername;
  private String fallbackPassword;

  void setFallbackCredentials(final String username, final String password) {
    this.fallbackUsername = username;
    this.fallbackPassword = password;
  }

  private static Logger log = getLogger(
      DefaultRabbitConnectionFactory.class);

  @Override
  protected Object determineCurrentLookupKey() {
    log.trace("Entering DefaultRabbitConnectionFactory.");

    String communityId = "default";

    log.info("DefaultRabbitConnectionFactory determineLookupKey With Key {}.", communityId);

    if (getTargetConnectionFactory(communityId) == null) {
      
      log.info("Connection Factory not found for Default, creating new one.");
      
      addTargetConnectionFactory(communityId,
          createRabbitConnectionFactory(
              consulService.getRabbitPropertiesConnectionFactory(communityId)));
    }

    log.info("Leaving DefaultRabbitConnectionFactory with tenant {}.",
        communityId);

    return communityId;
  }

  private ConnectionFactory createRabbitConnectionFactory(
      Map<String, Object> connectionFactoryProperties) {

    return ConnectionFactoryBuilder.create()
        .name((String) connectionFactoryProperties.get("name"))
        .host((String) connectionFactoryProperties.get("host"))
        .port((String) connectionFactoryProperties.get("port"))
        .vhost((String) connectionFactoryProperties.get("vhost"))
        .username(ConnectionFactoryBuilder.credential(connectionFactoryProperties.get("username"), fallbackUsername))
        .password(ConnectionFactoryBuilder.credential(connectionFactoryProperties.get("password"), fallbackPassword))
        .ssl(ConnectionFactoryBuilder.sslFrom(connectionFactoryProperties.get("ssl"), sslEnabled))
        .build();
  }
}
