package com.keenvil.event;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Event Module configuration properties.
 */
@Component
@ConfigurationProperties(prefix = "event")
public class EventProperties {

  private String host;
  private int port;
  private String vhost;
  private String username;
  private String password;
  private int maxConcurrentConsumers;

  public EventProperties() { }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getVhost() {
    return vhost;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public int getMaxConcurrentConsumers() {
    return maxConcurrentConsumers;
  }

  public void setHost(String theHost) {
    host = theHost;
  }

  public void setPort(int thePort) {
    port = thePort;
  }

  public void setVhost(String theVhost) {
    vhost = theVhost;
  }

  public void setUsername(String theUsername) {
    username = theUsername;
  }

  public void setPassword(String thePassword) {
    password = thePassword;
  }

  public void setMaxConcurrentConsumers(int theMaxConcurrentConsumers) {
    maxConcurrentConsumers = theMaxConcurrentConsumers;
  }
}
