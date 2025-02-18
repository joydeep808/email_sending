package com.interview.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

  private final EnvReader envReader;

  @Bean
  public RedisConnectionFactory connectionFactory() {
    String redisPassword = envReader.getEnvEntry("REDIS_PASSWORD");
    String redisHost = envReader.getEnvEntry("REDIS_HOST");
    String redisPort = envReader.getEnvEntry("REDIS_PORT");
    if (redisPassword == null || redisPassword.equals("")) {
      throw new RuntimeException("Please provide redis password");
    }
    if (redisHost == null || redisHost.equals("")) {
      throw new RuntimeException("Please provide redis host");
    }
    if (redisPort == null || redisHost.equals("")) {
      throw new RuntimeException("Please provide redis host");
    }

    RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
    standaloneConfiguration.setHostName(redisHost);
    standaloneConfiguration.setPort(Integer.parseInt(redisPort));
    standaloneConfiguration.setPassword(redisPassword);
    return new LettuceConnectionFactory(standaloneConfiguration);
  }

  @Bean
  public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, T> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Use StringRedisSerializer for keys (because keys are typically strings)
    template.setKeySerializer(new StringRedisSerializer());

    // Use GenericJackson2JsonRedisSerializer for values (handles complex objects
    // like TaskDto)
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    // Ensure Hash key and values also use Jackson (if you're working with hashes)
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    // Enable transaction support if needed
    template.setEnableTransactionSupport(true);

    template.afterPropertiesSet();
    return template;
  }

}