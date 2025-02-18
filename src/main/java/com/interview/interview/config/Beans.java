package com.interview.interview.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Beans {

  private final EnvReader envReader;

  @Bean
  public JavaMailSenderImpl javaMailSender() {
    String emailId = envReader.getEnvEntry("EMAIL_ID");
    String emailPassword = envReader.getEnvEntry("EMAIL_PASSWORD");
    if (emailId == null || emailId.equals("")) {
      throw new RuntimeException("Please provide your email id");
    }
    if (emailPassword == null || emailPassword.equals("")) {
      throw new RuntimeException("Please provide your email password");
    }
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost("smtp.gmail.com");
    javaMailSender.setPort(587);
    javaMailSender.setUsername(emailId);
    javaMailSender.setPassword(emailPassword);
    javaMailSender.setProtocol("smtp");

    javaMailSender.setJavaMailProperties(new Properties() {
      {
        put("mail.smtp.starttls.enable", "true");
        put("mail.smtp.auth", "true");
        // put("mail.debug", "true");
      }
      // i have to add the debug part

    });
    return javaMailSender;
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

}