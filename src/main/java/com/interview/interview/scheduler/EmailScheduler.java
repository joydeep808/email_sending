package com.interview.interview.scheduler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.interview.Constants;
import com.interview.interview.dto.FailedEmailTask;
import com.interview.interview.dto.TaskDto;
import com.interview.interview.rabbitmq.MessageSender;
import com.interview.interview.service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailScheduler {
  private final RedisService redisService;
  private final RabbitTemplate rabbitTemplate;
  private final MessageSender messageSender;
  private static final int BATCH_SIZE = 100;
  ExecutorService executorService = Executors.newFixedThreadPool(BATCH_SIZE);

  private final ObjectMapper objectMapper;

  @Scheduled(fixedDelay = 10000) // 10 seconds
  public void processEmails() {
    List<Object> pendingTasks = redisService.getBatchOfPendingTasks(Constants.PENDING_EMAILS,
        Constants.REDIS_PENDING_KEY_PREFIX, BATCH_SIZE);
    if (pendingTasks == null || pendingTasks.isEmpty() || pendingTasks.size() == 0) {
      return;
    }

    for (Object task : pendingTasks) {

      executorService.submit(() -> {
        TaskDto taskDto = (TaskDto) task;
        /**
         * To Prevent the email from being sent multiple times
         */
        try {
          // Mark as processing before sending to RabbitMQ
          // redisService.markTaskAsProcessing(taskDto);

          // Send to RabbitMQ
          redisService.deleteTask(taskDto.getId(), Constants.PENDING_EMAILS);
          messageSender.sendMessage(Constants.EMAIL_QUEUE, objectMapper.writeValueAsString(task));

          // Delete from Redis only after successful queue
        } catch (Exception e) {
          try {
            messageSender.sendMessage(Constants.FAILED_EMAIL_QUEUE, objectMapper.writeValueAsString(taskDto));
            return;
          } catch (JsonProcessingException e1) {

            log.error("Failed to process  task: {}", taskDto.getId(), e);
            return;
          }

          // Task will remain in PROCESSING state for recovery
        }
      });

    }
  }

  @Scheduled(fixedDelay = 10000)
  public void processFailedEmails() {
    List<Object> failedTasks = redisService.getBatchOfPendingTasks(Constants.FAILED_EMAILS,
        Constants.REDIS_FAILED_KEY_PREFIX, BATCH_SIZE);
    for (Object task : failedTasks) {
      executorService.submit(() -> {
        FailedEmailTask taskDto = (FailedEmailTask) task;
        try {
          // Mark as processing before sending to RabbitMQ
          redisService.markTaskAsProcessing(taskDto);

          // Send to RabbitMQ
          rabbitTemplate.convertAndSend(Constants.FAILED_EMAIL_QUEUE,
              objectMapper.writeValueAsString(task));

          // Delete from Redis only after successful queue
          redisService.deleteTask(taskDto.getId(), Constants.FAILED_EMAILS);
        } catch (Exception e) {
          log.error("Failed to process task: {}", taskDto.getId(), e);
        }
      });
    }
  }

}
