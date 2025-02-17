package com.interview.interview.entity;

import java.time.ZonedDateTime;

import com.interview.interview.Constants.TaskStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "emails")
public class EmailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String recipient;
  private String subject;
  private String body;
  @Enumerated(EnumType.STRING)
  private TaskStatus status;
  private Long createdAt;
  private Long updatedAt;
  private Long sentAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = ZonedDateTime.now().toInstant().toEpochMilli();
  }

  @PostUpdate
  public void postUpdate() {
    this.updatedAt = ZonedDateTime.now().toInstant().toEpochMilli();
  }
}
