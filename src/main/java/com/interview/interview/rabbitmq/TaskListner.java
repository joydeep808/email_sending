package com.interview.interview.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.interview.Constants;
import com.interview.interview.dto.TaskDto;
import com.interview.interview.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskListner {

  private final ObjectMapper objectMapper;
  private final EmailService emailService;

  @RabbitListener(queues = Constants.EMAIL_QUEUE, ackMode = "AUTO")
  public void processEmail(String message) {
    try {
      TaskDto value = objectMapper.readValue(message, TaskDto.class);
      // TaskDto task = objectMapper.readValue(value.toString(), TaskDto.class);
      emailService.sendEmail(value);

    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
      System.out.println("ISSUE");
    }
  }

}
