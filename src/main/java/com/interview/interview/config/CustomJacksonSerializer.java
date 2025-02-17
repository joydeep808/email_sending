// package com.interview.interview.config;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.interview.interview.dto.TaskDto;

// import org.springframework.data.redis.serializer.RedisSerializer;
// import org.springframework.data.redis.serializer.SerializationException;

// public class CustomJacksonSerializer<T> implements RedisSerializer<T> {
// private final ObjectMapper objectMapper = new ObjectMapper();

// @Override
// public byte[] serialize(T t) throws SerializationException {
// try {
// return objectMapper.writeValueAsBytes(t);
// } catch (Exception e) {
// throw new SerializationException("Could not serialize object", e);
// }
// }

// @Override
// public T deserialize(byte[] bytes) throws SerializationException {
// try {
// return objectMapper.readValue(bytes,
// objectMapper.getTypeFactory().constructType(TaskDto.class));
// } catch (Exception e) {
// throw new SerializationException("Could not deserialize object", e);
// }
// }
// }
