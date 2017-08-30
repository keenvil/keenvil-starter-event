package com.keenvil.event.domain;

import org.apache.commons.lang3.Validate;

public class EventHost {

  private String host;
  private int port;
  private String vhost;
  private String username;
  private String password;
  private int maxConcurrentConsumers;
  private boolean isDefault;
  private String name;

  public EventHost() { }

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
  
  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public String getName() {
    return name;
  }

  public void setName(String theName) {
    Validate.notBlank(theName, "Name cannot be empty.");
    name = theName;
  }
}
