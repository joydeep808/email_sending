package com.interview.interview.controller;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.interview.interview.dto.CreateEmailRequest;
import com.interview.interview.dto.TaskDto;
import com.interview.interview.service.EmailService;
import com.interview.interview.service.RedisService;
import com.interview.interview.util.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

  private final EmailService emailService;
  private final RedisService redisService;

  @PostMapping
  public ResponseEntity<Response<Object>> sendEmail(@RequestBody @Valid CreateEmailRequest emailRequest)
      throws UnsupportedEncodingException {
    return emailService
        .createTask(new TaskDto(emailRequest.getRecipient(), emailRequest.getSubject(), emailRequest.getBody()));
  }

  @PostMapping("/batch")
  public ResponseEntity<Response<Object>> sendBatchEmail(@RequestBody @Valid Set<CreateEmailRequest> emailRequests) {
    for (CreateEmailRequest createEmailRequest : emailRequests) {
      emailService.createTask(new TaskDto(createEmailRequest.getRecipient(), createEmailRequest.getSubject(),
          createEmailRequest.getBody()));
    }
    return new Response<>().sendSuccessResponse(200, "If everything is good you will recive an email");
  }

  @GetMapping
  public ResponseEntity<Response<Object>> getEmailStatus(@RequestParam(name = "id", required = true) String id)
      throws Exception {
    return emailService.getEmailStatus(id);
  }

  @GetMapping("/{email}")
  public ResponseEntity<Response<Object>> getEmailRecordByRecipient(@PathVariable(name = "email") String email)
      throws Exception {
    return emailService.getStatusByEmail(email);
  }

}
