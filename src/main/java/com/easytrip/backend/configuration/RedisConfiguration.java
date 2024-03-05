package com.easytrip.backend.configuration;

import com.easytrip.backend.weather.dto.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.host}")
  private String host;


  @Bean
  public RedisConnectionFactory redisConnectionFactory() {

    return new LettuceConnectionFactory(host, port);
  }

  @Bean
  @Primary
  public RedisTemplate<String, Object> redisTemplate() {

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }

  @Bean
  public RedisTemplate<String, WeatherDto> redisWeatherTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, WeatherDto> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(WeatherDto.class));
    return template;
  }
}
