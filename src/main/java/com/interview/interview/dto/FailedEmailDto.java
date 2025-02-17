package com.interview.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedEmailDto {
  private String id;
  private String recipient;
  private String subject;
  private String body;
  private int counter;
}
