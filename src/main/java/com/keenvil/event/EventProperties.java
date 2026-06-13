package com.keenvil.event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.keenvil.cork.error.KeenvilException;
import com.keenvil.event.domain.EventHost;

/**
 * Event Module configuration properties.
 */
@Component
@ConfigurationProperties(prefix = "event")
public class EventProperties {

  private List<EventHost> eventHosts;
  
  private EventHost defaultHost;
  
  @PostConstruct
  public void init() {
    if(eventHosts != null) {
      List<EventHost> defaults = eventHosts.stream()
          .filter(tc -> tc.isDefault())
          .collect(Collectors.toCollection(ArrayList::new));
    
      if (defaults.size() != 1) {
        throw new KeenvilException("Only one event host  must be set"
            + " as default");
      }
      
      defaultHost = defaults.get(0);
    }
  }
  
  public void setEventHosts(List<EventHost> hosts) {
    eventHosts = hosts;
  }
  
  public List<EventHost> getEventHosts() {
    return eventHosts;
  }
  
  public EventHost getDefaultHost() {
    return defaultHost;
  }
}
