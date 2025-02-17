package com.interview.interview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Constants {
  public final static String EMAIL_QUEUE = "email_send_queue";
  public final static String FAILED_EMAIL_QUEUE = "failed_email_queue";
  public final static String SAVE_QUEUE = "save_queue";
  public static final String PROCESSING_EMAILS = "processing_emails";
  public final static String REDIS_PENDING_KEY_PREFIX = "PENDING:";
  public final static String REDIS_PROCESSING_KEY_PREFIX = "PROCESSING:";

  public final static String REDIS_FAILED_KEY_PREFIX = "FAILED:";
  public static final int BATCH_SIZE = 10;
  public static final String EMAIL_PREFIX = "email:";
  public static final String PENDING_EMAILS = "pending_emails";
  public static final String FAILED_EMAILS = "failed_emails";
  public static final String EMAIL_TASKS_KEY = "email_tasks";

  public enum TaskStatus {
    PENDING("PENDING"),
    SENT("SENT"),
    FAILED("FAILED"),
    PROCESSING("PROCESSING");

    private String status;

    TaskStatus(String status) {
      this.status = status;
    }

    @JsonValue
    public String getStatus() {
      return status;
    }

    @JsonCreator
    public static TaskStatus fromString(String status) {
      for (TaskStatus taskStatus : TaskStatus.values()) {
        if (taskStatus.status.equalsIgnoreCase(status)) {
          return taskStatus;
        }
      }
      return null; // or throw an exception if invalid status
    }
  }

}
