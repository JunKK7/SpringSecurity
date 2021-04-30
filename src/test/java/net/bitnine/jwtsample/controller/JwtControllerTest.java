package net.bitnine.jwtsample.controller;

import lombok.extern.slf4j.Slf4j;
import net.bitnine.JwtSampleApplication;
import net.bitnine.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@Slf4j
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {RedisConfig.class, RedisAutoConfiguration.class})
class JwtControllerTest {

  @Autowired
  StringRedisTemplate redisTemplate;

  @Test
  public void redisTest(){
    //StringRedisTemplate redisTemplate = new StringRedisTemplate();

    String name = "user1";
    String token = "1231233123123";

    String name2 = "user2";
    String token2 = "131312312333123131";

    SetOperations<String ,String> redisSetOp = redisTemplate.opsForSet();

    redisSetOp.add(name,token);
    redisSetOp.add(name2,token2);

    Cursor<String> cursor = redisSetOp.scan(name2, ScanOptions.scanOptions().match("*").count(1).build());
    while(cursor.hasNext()){
      log.info(cursor.next());
    }
  }

}