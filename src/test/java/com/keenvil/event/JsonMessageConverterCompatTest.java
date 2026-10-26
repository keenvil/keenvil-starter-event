package com.keenvil.event;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * Boot 4 (§9.20): el converter de Jackson 3 tiene que producir el mismo JSON y los mismos headers de tipo
 * que el Jackson2JsonMessageConverter de los servicios que siguen en Boot 3, y leer lo que ellos mandan.
 */
public class JsonMessageConverterCompatTest {

  public enum Kind { ENTRY, EXIT }

  public static class Sample {
    public Long id = 42L;
    public String name = "Ana";
    public Kind kind = Kind.ENTRY;
    public Date at = new Date(1793047775746L);
    public LocalDateTime when = LocalDateTime.of(2026, 10, 26, 19, 30, 5);
    public LocalDate day = LocalDate.of(2026, 10, 26);
    public BigDecimal amount = new BigDecimal("1500.50");
    public List<String> tags = List.of("a", "b");
    public String empty;
  }

  private final MessageConverter boot4 = new EventConfiguration().jsonMessageConverter();

  @SuppressWarnings("removal")
  private final MessageConverter boot3 = new Jackson2JsonMessageConverter();

  @Test
  public void mismoJsonYMismoTipoQueBoot3() {
    Message nuevo = boot4.toMessage(new Sample(), new MessageProperties());
    Message viejo = boot3.toMessage(new Sample(), new MessageProperties());
    assertEquals(new String(viejo.getBody(), StandardCharsets.UTF_8), new String(nuevo.getBody(), StandardCharsets.UTF_8));
    assertEquals(viejo.getMessageProperties().getHeaders().get("__TypeId__"),
        nuevo.getMessageProperties().getHeaders().get("__TypeId__"));
    assertEquals(viejo.getMessageProperties().getContentType(), nuevo.getMessageProperties().getContentType());
  }

  @Test
  public void leeLoQueMandaBoot3IgnorandoCamposNuevos() {
    Message viejo = boot3.toMessage(new Sample(), new MessageProperties());
    String body = new String(viejo.getBody(), StandardCharsets.UTF_8).replaceFirst("[{]","{\"campoNuevo\":1,");
    // Como un @RabbitListener: el tipo sale del parametro del metodo (igual que en Boot 3).
    viejo.getMessageProperties().setInferredArgumentType(Sample.class);
    Message conCampoNuevo = new Message(body.getBytes(StandardCharsets.UTF_8), viejo.getMessageProperties());
    Sample leido = (Sample) boot4.fromMessage(conCampoNuevo);
    Sample esperado = new Sample();
    assertEquals(esperado.when, leido.when);
    assertEquals(esperado.at, leido.at);
    assertEquals(esperado.day, leido.day);
    assertEquals(esperado.amount, leido.amount);
    assertEquals(esperado.kind, leido.kind);
  }
}
