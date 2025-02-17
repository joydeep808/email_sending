package com.interview.interview.service;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.interview.interview.Constants;
import com.interview.interview.Constants.TaskStatus;
import com.interview.interview.config.EnvReader;
import com.interview.interview.dto.BaseTask;
import com.interview.interview.dto.FailedEmailTask;
import com.interview.interview.dto.TaskDto;
import com.interview.interview.entity.EmailEntity;
import com.interview.interview.rabbitmq.MessageSender;
import com.interview.interview.repository.EmailRepo;
import com.interview.interview.util.Response;

import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
  private final JavaMailSenderImpl javaMailSender;
  private final EnvReader envReader;
  private final RedisService redisService;
  private final MessageSender messageSender;
  private final EmailRepo emailRepo;

  public ResponseEntity<Response<Object>> createTask(TaskDto task) {
    boolean isSet = redisService.saveTask(task, Constants.PENDING_EMAILS);
    if (isSet) {
      return new Response<>().sendSuccessResponse(201, "Task set successfully", task);
    }
    return new Response<>().sendErrorResponse(400, "Task already present");
  }

  public boolean sendEmail(BaseTask task) {
    try {
      sendEmailUtil(task);
      return true;
    } catch (Exception e) {
      failedEmail(task, 1);
      return false;
    }

  }

  public void sendFailedEmail(FailedEmailTask task) {
    try {
      sendEmailUtil(task);
      log.info("Email sent successfully");
      return;
    } catch (Exception e) {
      failedEmail(task, 1);
      log.error("Failed to send email");
      return;
    }

  }

  private <T extends BaseTask> void sendEmailUtil(T task) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
      mimeMessageHelper.setSubject(task.getSubject());
      mimeMessageHelper
          .setFrom(new InternetAddress(envReader.getEnvEntry("EMAIL_ID"), envReader.getEnvEntry("USER_NAME")));
      mimeMessageHelper.setTo(task.getRecipient());
      mimeMessageHelper.setText(task.getBody(), true);

      // Attempt to send the email
      javaMailSender.send(message);

      successEmail(task);
      log.info("Email sent successfully");
    } catch (SendFailedException e) {
      // Check if the failure is because the email does not exist
      log.error("Send failed for email: {}. Reason: {}", task.getRecipient(), e.getMessage());
      failedEmail(task, 1);
    } catch (MessagingException e) {
      log.error("Messaging exception for email: {}. Reason: {}", task.getRecipient(), e.getMessage());
      failedEmail(task, 1);
    } catch (Exception e) {
      log.error("General exception: {}", e.getMessage(), e);
      failedEmail(task, 1);
    }
  }

  private void successEmail(BaseTask taskDto) {
    emailRepo
        .save(EmailEntity.builder().body(taskDto.getBody()).status(TaskStatus.SENT).sentAt(System.currentTimeMillis())
            .id(taskDto.getId()).recipient(taskDto.getRecipient()).subject(taskDto.getSubject()).build());
    return;
  }

  private void failedEmail(BaseTask taskDto, int counter) {
    redisService.saveTask(new FailedEmailTask(taskDto, counter), Constants.FAILED_EMAILS);
    log.error("email failed and it goes failed queue");
    return;
  }

}