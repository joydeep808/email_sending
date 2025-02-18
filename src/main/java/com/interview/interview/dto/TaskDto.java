package com.interview.interview.dto;

import com.fasterxml.jackson.annotation.*;
import com.interview.interview.Constants.TaskStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.time.ZonedDateTime;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskDto extends BaseTask {
  private Long sentAt;

  public TaskDto(String recipient, String subject, String body) {
    this.setSentAt(sentAt);
    this.setRecipient(recipient);
    this.setSubject(subject);
    this.setBody(body);
    this.setStatus(TaskStatus.PENDING);
    this.setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli());
    this.setId(UUID.randomUUID().toString());
  }

  @JsonCreator
  public TaskDto(
      @JsonProperty("id") String id,
      @JsonProperty("recipient") String recipient,
      @JsonProperty("subject") String subject,
      @JsonProperty("body") String body,
      @JsonProperty("status") TaskStatus status,
      @JsonProperty("createdAt") Long createdAt,
      @JsonProperty("sentAt") Long sentAt) {
    this.setSentAt(sentAt);
    this.setRecipient(recipient);
    this.setSubject(subject);
    this.setBody(body);
    this.setStatus(status);
    this.setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli());
    this.setId(id);
  }

}
