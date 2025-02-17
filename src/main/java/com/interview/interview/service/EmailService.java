package com.interview.interview.service;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.interview.interview.Constants;
import com.interview.interview.config.EnvReader;
import com.interview.interview.dto.TaskDto;
import com.interview.interview.util.Response;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final JavaMailSenderImpl javaMailSender;
  private final EnvReader envReader;
  private final RedisService redisService;

  public ResponseEntity<Response<Object>> createTask(TaskDto task) {
    boolean isSet = redisService.saveTask(task);
    if (isSet) {
      return new Response<>().sendSuccessResponse(201, "Task set successfully", task);
    }
    return new Response<>().sendErrorResponse(400, "Task already present");
  }

  public boolean sendEmail(TaskDto task) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
      mimeMessageHelper.setSubject(task.getSubject());
      mimeMessageHelper
          .setFrom(new InternetAddress(envReader.getEnvEntry("EMAIL_ID"), envReader.getEnvEntry("USER_NAME")));
      mimeMessageHelper.setTo(task.getRecipient());
      mimeMessageHelper.setText(task.getBody(), true);
      javaMailSender.send(message);
      return true;
    } catch (Exception e) {
      failedEmail(task);
      return false;
    }

  }

  private void successEmail() {
    // the email will automatically
    // do something
  }

  private void failedEmail(TaskDto taskDto) {
    // do something
    // in the failed email i have to put the email into the failed email queue
    // i have to check the counter whether it excedeed or not
    // if excedeed then i have to save the email into the database
    // if
  }

}