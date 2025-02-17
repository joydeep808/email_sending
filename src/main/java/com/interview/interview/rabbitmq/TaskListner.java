package com.interview.interview.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.interview.Constants;
import com.interview.interview.dto.FailedEmailTask;
import com.interview.interview.dto.TaskDto;
import com.interview.interview.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskListner {

  private final ObjectMapper objectMapper;
  private final EmailService emailService;

  @RabbitListener(queues = Constants.EMAIL_QUEUE, ackMode = "AUTO")
  public void processEmail(String message) {
    try {
      TaskDto value = objectMapper.readValue(message, TaskDto.class);
      emailService.sendEmail(value);
      log.info("Email sent successfully valid email");
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
    }
  }

  @RabbitListener(queues = Constants.FAILED_EMAIL_QUEUE, ackMode = "AUTO")
  public void processFailedEmails(String message) {
    try {
      FailedEmailTask value = objectMapper.readValue(message, FailedEmailTask.class);
      emailService.sendFailedEmail(value);
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
    }

  }

}
