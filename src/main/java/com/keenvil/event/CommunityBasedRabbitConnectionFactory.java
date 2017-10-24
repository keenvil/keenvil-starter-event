package com.keenvil.event;

import com.keenvil.cork.CommunityIdentifierResolver;
import com.keenvil.cork.consul.ConsulService;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.AbstractRoutingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class CommunityBasedRabbitConnectionFactory
    extends AbstractRoutingConnectionFactory {

  @Autowired
  private CommunityIdentifierResolver communityResolver;

  @Autowired
  ConsulService consulService;

  private static Logger log = getLogger(
      CommunityBasedRabbitConnectionFactory.class);

  @Override
  protected Object determineCurrentLookupKey() {
    log.trace("Entering CommunityBasedRabbitConnectionFactory.");

    String communityId = communityResolver.resolve();

    if (getTargetConnectionFactory(communityId) == null) {
      addTargetConnectionFactory(communityId,
          createRabbitConnectionFactory(
              consulService.getRabbitPropertiesConnectionFactory(communityId)));
    }

    log.trace("Leaving CommunityBasedRabbitConnectionFactory with tenant {}.",
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
