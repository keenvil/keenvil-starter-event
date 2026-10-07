package com.keenvil.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.keenvil.event.domain.Template;

/**
 * Enables Micrometer observation on every {@link Template} bean (DefaultTemplate,
 * CommunityBasedTemplate) so the trace context travels in the AMQP headers (W3C traceparent).
 * Registered only with {@code event.observation.enabled=true}.
 */
public class EventObservationPostProcessor implements BeanPostProcessor, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    if (bean instanceof Template template) {
      template.enableObservation(applicationContext);
    }
    return bean;
  }
}
