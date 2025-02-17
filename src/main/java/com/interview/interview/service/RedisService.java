package com.interview.interview.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.interview.Constants;
import com.interview.interview.Constants.TaskStatus;
import com.interview.interview.dto.BaseTask;
import com.interview.interview.dto.FailedEmailTask;
import com.interview.interview.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  public boolean set(String key, TaskDto value) {
    try {
      redisTemplate.opsForValue().set(Constants.REDIS_PENDING_KEY_PREFIX + key, objectMapper.writeValueAsString(value));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean setTask(TaskDto task) {
    Map<String, String> emailData = new HashMap<>();
    emailData.put("id", task.getId());
    emailData.put("recipient", task.getRecipient());
    emailData.put("subject", task.getSubject());
    emailData.put("body", task.getBody());
    emailData.put("status", TaskStatus.PENDING.toString());
    emailData.put("createdAt", task.getCreatedAt().toString());

    // Use single operation to set all hash fields
    redisTemplate.opsForHash().putAll(Constants.EMAIL_PREFIX + task.getId(), emailData);

    // Add to pending set with score as creation time
    redisTemplate.opsForZSet().add(Constants.PENDING_EMAILS, task.getId(), task.getCreatedAt());
    return true;

  }

  public <T> T get(String key, Class<T> clazz) {
    try {
      return objectMapper.convertValue(redisTemplate.opsForValue().get(key), clazz);
    } catch (Exception e) {
      return null;
    }
  }

  public List<Object> getAll(List<String> keys) {
    return redisTemplate.opsForValue().multiGet(keys);
  }

  public boolean delete(String key) {
    return redisTemplate.delete(key);
  }

  public boolean exists(String key) {
    try {
      return redisTemplate.hasKey(Constants.REDIS_PENDING_KEY_PREFIX + key);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean update(String key, String value) {
    try {
      redisTemplate.opsForValue().setIfAbsent(key, value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Set<String> isKeysAreAvilable() {
    return redisTemplate.keys(Constants.REDIS_PENDING_KEY_PREFIX + "*");

  }

  public String generateKeyForRedis(String status, String email, String purpose) {
    return status + ":" + email + "_" + purpose;
  }

  public void deleteAll() {
    redisTemplate.delete(redisTemplate.keys(Constants.REDIS_PENDING_KEY_PREFIX + "*"));
    redisTemplate.delete(redisTemplate.keys(Constants.EMAIL_PREFIX + "*"));
    redisTemplate.delete(redisTemplate.keys(Constants.PROCESSING_EMAILS));
    redisTemplate.delete(redisTemplate.keys(Constants.FAILED_EMAILS));
    redisTemplate.delete(redisTemplate.keys(Constants.PENDING_EMAILS));
  }

  public List<String> limitKeys(List<String> keys) {
    return keys.subList(0, Constants.BATCH_SIZE);
  }

  public <T extends BaseTask> boolean saveTask(T task, String type) {
    if (type.equals(Constants.FAILED_EMAILS)) {
      boolean pendingTaskExists = isTaskExistsInRedis(Constants.PENDING_EMAILS, task.getRecipient());
      if (pendingTaskExists) {
        return false;
      }
      String id = task.getId();
      redisTemplate.opsForHash().put(Constants.REDIS_FAILED_KEY_PREFIX, id, task);
      redisTemplate.opsForZSet().add(Constants.FAILED_EMAILS, id,
          task.getCreatedAt());
      return true;
    }

    boolean pendingTaskExists = isTaskExistsInRedis(Constants.PENDING_EMAILS, task.getRecipient());

    if (pendingTaskExists) {
      return false;
    } else {
      String id = task.getId();
      redisTemplate.opsForHash().put(Constants.REDIS_PENDING_KEY_PREFIX, id, task);
      redisTemplate.opsForZSet().add(Constants.PENDING_EMAILS, id,
          task.getCreatedAt());

      return true;
    }
  }

  public boolean saveFailedTask(FailedEmailTask task) {
    boolean pendingTaskExists = isTaskExistsInRedis(Constants.FAILED_EMAILS, task.getRecipient());
    if (pendingTaskExists) {
      return false;
    } else {
      // redisTemplate.opsForHash().put(Constants.REDIS_PENDING_KEY_PREFIX,
      // task.getId(), task);
      // redisTemplate.opsForZSet().add(Constants.FAILED_EMAILS, task.getId(),
      // Instant.now().getEpochSecond());
      //
      return true;
    }
  }

  private boolean isTaskExistsInRedis(String taskType, String recipient) {
    // Check in pending emails sorted set
    try {
      return redisTemplate.opsForHash().hasKey(taskType, recipient);
    } catch (Exception e) {
      return true;
      // TODO: handle exception
    }
    // Set<Object> pendingIds =
    // redisTemplate.opsForZSet().range(Constants.PENDING_EMAILS, 0, -1);
    // if (pendingIds == null || pendingIds.size() == 0)
    // return false;

    // return pendingIds.stream().anyMatch(id -> ((String) id).equals(recipient));
  }

  public List<Object> getBatchOfPendingTasks(String emailType, String prefix, int batchSize) {
    // Get oldest pending tasks based on creation time
    Set<Object> pendingIds = redisTemplate.opsForZSet()
        .range(emailType, 0, batchSize - 1);

    if (pendingIds == null || pendingIds.isEmpty()) {
      return Collections.emptyList();
    }

    // List<Object> task =
    // redisTemplate.opsForHash().multiGet(Constants.REDIS_PENDING_KEY_PREFIX,
    // pendingIds);
    List<Object> task = redisTemplate.opsForHash().multiGet(prefix, pendingIds);
    if (task.size() == 0) {
      redisTemplate.opsForZSet().remove(emailType, pendingIds);
      return Collections.emptyList();

    }

    return task;
  }

  // return tasks;

  public <T extends BaseTask> void markTaskAsProcessing(T task) {
    if (task != null) {
      // Update task status
      Long remove = redisTemplate.opsForZSet().remove(Constants.PENDING_EMAILS, task.getId());
      System.out.println(remove);
      task.setStatus(TaskStatus.PROCESSING);
      redisTemplate.opsForHash().put(Constants.REDIS_PROCESSING_KEY_PREFIX, task.getId(), task);
      // Move from pending to processing sorted set
      Boolean boolean1 = redisTemplate.opsForZSet().add(Constants.PROCESSING_EMAILS, task.getId(), task.getCreatedAt());
      System.out.println(boolean1);
    }
  }

  public void deleteTask(String taskId) {
    try {
      // Delete from the sorted set
      redisTemplate.opsForZSet().remove(Constants.PENDING_EMAILS, taskId);
      redisTemplate.opsForHash().delete(Constants.REDIS_PENDING_KEY_PREFIX, taskId);

    } catch (Exception e) {
      log.error("Error deleting task with ID: {}", taskId, e);
    }
  }

  // public void markTaskAsProcessing(String taskId) {
  // TaskDto task = (TaskDto)
  // redisTemplate.opsForHash().get(Constants.REDIS_PENDING_KEY_PREFIX, taskId);
  // if (task != null) {
  // task.setStatus(TaskStatus.PROCESSING);
  // redisTemplate.opsForHash().put(Constants.REDIS_PENDING_KEY_PREFIX, taskId,
  // task);
  // }
  // }

}
