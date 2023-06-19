package com.keenvil.event;

import com.keenvil.cork.consul.ConsulService;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.AbstractRoutingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultRabbitConnectionFactory
    extends AbstractRoutingConnectionFactory {


  @Autowired(required = false)
  ConsulService consulService;

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
        .username((String) connectionFactoryProperties.get("username"))
        .password((String) connectionFactoryProperties.get("password"))
        .build();
  }
}
