package com.interview.interview.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageSender {
  private final RabbitTemplate template;

  public boolean sendMessage(String queue, String message) {
    try {
      template.convertAndSend(queue, message);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
