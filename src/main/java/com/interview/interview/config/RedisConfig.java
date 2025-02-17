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
    RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
    standaloneConfiguration.setHostName(envReader.getEnvEntry("REDIS_HOST"));
    standaloneConfiguration.setPort(22612);
    standaloneConfiguration.setPassword("AVNS_c2ZjB75TzYcZ8a4kkwb");
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