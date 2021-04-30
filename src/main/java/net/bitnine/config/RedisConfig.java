package net.bitnine.config;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.embedded.RedisServer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  private RedisServer redisServer;

  @PostConstruct
  public void RedisConfig(){
    redisServer = new RedisServer(port);
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis(){
    redisServer.stop();
  }

  @PostConstruct
  public void startRedis(){
  }
  @Bean
  public RedisConnectionFactory redisConnectionFactory(){
    return new LettuceConnectionFactory(host,port);
  }

  @Bean
  public RedisTemplate<?,?> redisTemplate(){
    RedisTemplate<byte[],byte[]> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }
}
