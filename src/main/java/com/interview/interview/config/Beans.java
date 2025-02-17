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
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost("smtp.gmail.com");
    javaMailSender.setPort(587);
    javaMailSender.setUsername(envReader.getEnvEntry("EMAIL_ID"));
    javaMailSender.setPassword(envReader.getEnvEntry("EMAIL_PASSWORD"));
    javaMailSender.setProtocol("smtp");

    javaMailSender.setJavaMailProperties(new Properties() {
      {
        put("mail.smtp.starttls.enable", "true");
        put("mail.smtp.auth", "true");
        put("mail.debug", "true");
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