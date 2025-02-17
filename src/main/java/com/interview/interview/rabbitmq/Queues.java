package com.interview.interview.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.interview.interview.Constants;

@Configuration
@EnableRabbit
public class Queues {

  // Email Queue Setup
  @Bean
  public Queue emailQueue() {
    return new Queue(Constants.EMAIL_QUEUE, true);
  }

  @Bean
  public Queue failedEmailQueue() {
    return new Queue(Constants.FAILED_EMAIL_QUEUE, true);
  }

  @Bean
  public Queue taskSaveQueue() {
    return new Queue(Constants.SAVE_QUEUE, true);
    // i have to save all the tasks in the database
    // whether it is succcess or failed i have to set all the things
  }

}
