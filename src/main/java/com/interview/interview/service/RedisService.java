package com.interview.interview.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.interview.Constants;
import com.interview.interview.Constants.TaskStatus;
import com.interview.interview.dto.TaskDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;
  private final RedisTemplate<String, Object> stringRedisTemplate;

  public boolean set(String key, TaskDto value) {
    try {
      redisTemplate.opsForValue().set(Constants.REDIS_KEY_PREFIX + key, objectMapper.writeValueAsString(value));
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
      return redisTemplate.hasKey(Constants.REDIS_KEY_PREFIX + key);
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
    return redisTemplate.keys(Constants.REDIS_KEY_PREFIX + "*");

  }

  public String generateKeyForRedis(String status, String email, String purpose) {
    return status + ":" + email + "_" + purpose;
  }

  public void deleteAll() {
    redisTemplate.delete(redisTemplate.keys(Constants.REDIS_KEY_PREFIX + "*"));
  }

  public List<String> limitKeys(List<String> keys) {
    return keys.subList(0, Constants.BATCH_SIZE);
  }

  public boolean saveTask(TaskDto task) {
    boolean pendingTaskExists = isPendingTaskExists(task.getRecipient());
    System.out.println(pendingTaskExists);
    if (pendingTaskExists) {
      return false;
    } else {
      redisTemplate.opsForHash().put(Constants.REDIS_KEY_PREFIX, task.getId(), task);
      redisTemplate.opsForZSet().add(Constants.PENDING_EMAILS, task.getId(),
          task.getCreatedAt());
      return true;
    }
  }

  private boolean isPendingTaskExists(String recipient) {
    // Check in pending emails sorted set
    try {
      return redisTemplate.opsForHash().hasKey(Constants.PENDING_EMAILS, recipient);
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

  public List<Object> getBatchOfPendingTasks(int batchSize) {
    // Get oldest pending tasks based on creation time
    Set<Object> pendingIds = redisTemplate.opsForZSet()
        .range(Constants.PENDING_EMAILS, 0, batchSize - 1);

    if (pendingIds == null || pendingIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<Object> task = redisTemplate.opsForHash().multiGet(Constants.REDIS_KEY_PREFIX, pendingIds);
    return task;
  }

  // return tasks;

  public void markTaskAsProcessing(String taskId) {
    TaskDto task = (TaskDto) redisTemplate.opsForHash().get(Constants.REDIS_KEY_PREFIX, taskId);
    if (task != null) {
      // Update task status
      task.setStatus(TaskStatus.PROCESSING);
      redisTemplate.opsForHash().put(Constants.REDIS_KEY_PREFIX, taskId, task);

      // Move from pending to processing sorted set
      redisTemplate.opsForZSet().remove(Constants.PENDING_EMAILS, taskId);
      redisTemplate.opsForZSet().add(Constants.PROCESSING_EMAILS, taskId, task.getCreatedAt());
    }
  }

  public void deleteTask(String taskId) {
    // Remove from hash
    redisTemplate.opsForHash().delete(Constants.REDIS_KEY_PREFIX, taskId);

    // Remove from sorted sets
    redisTemplate.opsForZSet().remove(Constants.PENDING_EMAILS, taskId);
    redisTemplate.opsForZSet().remove(Constants.PROCESSING_EMAILS, taskId);
  }

  // public void markTaskAsProcessing(String taskId) {
  // TaskDto task = (TaskDto)
  // redisTemplate.opsForHash().get(Constants.REDIS_KEY_PREFIX, taskId);
  // if (task != null) {
  // task.setStatus(TaskStatus.PROCESSING);
  // redisTemplate.opsForHash().put(Constants.REDIS_KEY_PREFIX, taskId, task);
  // }
  // }

}
