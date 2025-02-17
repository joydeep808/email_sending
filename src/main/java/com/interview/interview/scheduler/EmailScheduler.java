package com.interview.interview.scheduler;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.interview.Constants;
import com.interview.interview.dto.TaskDto;
import com.interview.interview.service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailScheduler {
  private final RedisService redisService;
  private final RabbitTemplate rabbitTemplate;
  private static final int BATCH_SIZE = 100;

  private final ObjectMapper objectMapper;

  @Scheduled(fixedDelay = 10000) // 10 seconds
  public void processEmails() {
    List<Object> pendingTasks = redisService.getBatchOfPendingTasks(BATCH_SIZE);

    for (Object task : pendingTasks) {
      TaskDto taskDto = (TaskDto) task;
      try {
        // Mark as processing before sending to RabbitMQ
        redisService.markTaskAsProcessing(taskDto.getId());

        // Send to RabbitMQ
        rabbitTemplate.convertAndSend(Constants.EMAIL_QUEUE, objectMapper.writeValueAsString(task));

        // Delete from Redis only after successful queue
        redisService.deleteTask(taskDto.getId());
      } catch (Exception e) {
        log.error("Failed to process task: {}", taskDto.getId(), e);
        // Task will remain in PROCESSING state for recovery
      }
    }
  }

}