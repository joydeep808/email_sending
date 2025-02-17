package com.interview.interview.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.interview.interview.dto.TaskDto;
import com.interview.interview.service.EmailService;
import com.interview.interview.service.RedisService;
import com.interview.interview.util.Response;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

  private final EmailService emailService;
  private final RedisService redisService;

  @GetMapping("/send")
  public ResponseEntity<Response<Object>> sendEmail(@RequestParam String recipient, @RequestParam String subject,
      @RequestParam String body) throws UnsupportedEncodingException {
    return emailService.createTask(new TaskDto(recipient, subject, body));
  }

  @GetMapping("/delete")
  public boolean deleteEmail() {
    redisService.deleteAll();

    return true;
  }

}
