package com.keenvil.event;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.AbstractRoutingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.keenvil.cork.CommunityIdentifierResolver;

public class CommunityBasedRabbitConnectionFactory extends AbstractRoutingConnectionFactory {

  @Autowired
  private CommunityIdentifierResolver communityResolver;
  
  private static Logger log = getLogger(CommunityBasedRabbitConnectionFactory.class);
  
  @Override
  protected Object determineCurrentLookupKey() {
    log.trace("Entering CommunityBasedRabbitConnectionFactory.");

    String communityId = communityResolver.resolve();

    log.trace("Leaving CommunityBasedRabbitConnectionFactory with tenant {}.",
        communityId);

    return communityId;
  }

}
