package com.keenvil.event;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import com.keenvil.event.domain.Template;

public class EventObservationPostProcessorTest {

  static class RecordingTemplate implements Template {
    ApplicationContext context;

    @Override
    public void setExchange(final String exchange) {
    }

    @Override
    public void send(final String key, final Object message) {
    }

    @Override
    public void enableObservation(final ApplicationContext applicationContext) {
      context = applicationContext;
    }
  }

  @Test
  public void enablesObservationOnTemplates() {
    StaticApplicationContext context = new StaticApplicationContext();
    EventObservationPostProcessor processor = new EventObservationPostProcessor();
    processor.setApplicationContext(context);
    RecordingTemplate template = new RecordingTemplate();

    Object result = processor.postProcessAfterInitialization(template, "template");

    assertSame(template, result);
    assertSame(context, template.context);
  }

  @Test
  public void leavesOtherBeansUntouched() {
    EventObservationPostProcessor processor = new EventObservationPostProcessor();
    Object bean = new Object();
    assertSame(bean, processor.postProcessAfterInitialization(bean, "other"));
  }

  @Test
  public void rabbitTemplateAcceptsObservation() {
    // la API de Spring AMQP que usan DefaultTemplate/CommunityBasedTemplate existe y no falla sin registry
    RabbitTemplate rabbit = new RabbitTemplate((ConnectionFactory) null);
    rabbit.setApplicationContext(new StaticApplicationContext());
    rabbit.setObservationEnabled(true);
    assertTrue(true);
  }
}
