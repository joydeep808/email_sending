package com.interview.interview.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmailRequest {
  @NotNull(message = "Email should not be null")
  @Email(message = "Please enter valid email")
  private String recipient;
  @NotNull(message = "Subject is required")
  private String subject;
  @NotNull(message = "Email body is required")
  private String body;

}
