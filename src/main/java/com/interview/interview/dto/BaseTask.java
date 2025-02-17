package com.interview.interview.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.interview.Constants.TaskStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseTask {
  private String id;
  @NotNull(message = "recipient is required")
  @Email(message = "recipient must be a valid email address")
  private String recipient;

  @NotNull(message = "subject is required")
  private String subject;

  @NotNull(message = "body is required")
  private String body;

  private TaskStatus status;
  private Long createdAt;

  public BaseTask(String recipient, String subject, String body) {
    this.id = UUID.randomUUID().toString();
    this.recipient = recipient;
    this.subject = subject;
    this.body = body;
    this.status = TaskStatus.PENDING;
    this.createdAt = ZonedDateTime.now().toInstant().toEpochMilli();
  }

  // Default constructor for Jackson
  @JsonCreator
  public BaseTask(
      @JsonProperty("id") String id,
      @JsonProperty("recipient") String recipient,
      @JsonProperty("subject") String subject,
      @JsonProperty("body") String body,
      @JsonProperty("status") TaskStatus status,
      @JsonProperty("createdAt") Long createdAt,
      @JsonProperty("sentAt") Long sentAt) {
    this.id = id;
    this.recipient = recipient;
    this.subject = subject;
    this.body = body;
    this.status = status != null ? status : TaskStatus.PENDING;
    this.createdAt = createdAt != null ? createdAt : ZonedDateTime.now().toInstant().toEpochMilli();
  }
}
