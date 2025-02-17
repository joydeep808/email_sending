package com.interview.interview.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.interview.Constants.TaskStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FailedEmailTask extends BaseTask {
  private int counter;

  public FailedEmailTask(BaseTask baseTask) {
    this.setId(baseTask.getId());
    this.setRecipient(baseTask.getRecipient());
    this.setSubject(baseTask.getSubject());
    this.setBody(baseTask.getBody());
    this.setStatus(baseTask.getStatus());
    this.setCreatedAt(baseTask.getCreatedAt());
    this.setCounter(1);
  }

  public FailedEmailTask(BaseTask baseTask, int counter) {
    this.setId(baseTask.getId());
    this.setRecipient(baseTask.getRecipient());
    this.setSubject(baseTask.getSubject());
    this.setBody(baseTask.getBody());
    this.setStatus(baseTask.getStatus());
    this.setCreatedAt(baseTask.getCreatedAt());
    this.setCounter(counter);
  }

  @JsonCreator
  public FailedEmailTask(
      @JsonProperty("id") String id,
      @JsonProperty("recipient") String recipient,
      @JsonProperty("subject") String subject,
      @JsonProperty("body") String body,
      @JsonProperty("status") TaskStatus status,
      @JsonProperty("createdAt") Long createdAt,
      @JsonProperty("counter") Integer counter) {
    this.setCounter(counter);
    this.setRecipient(recipient);
    this.setSubject(subject);
    this.setBody(body);
    this.setStatus(TaskStatus.PENDING);
    this.setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli());
    this.setId(UUID.randomUUID().toString());
  }
}
