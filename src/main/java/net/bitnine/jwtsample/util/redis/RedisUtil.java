package net.bitnine.jwtsample.util.redis;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {

  private StringRedisTemplate redisTemplate;
  private SetOperations<String, String> redisSetOp;

  @Autowired
  public RedisUtil(StringRedisTemplate redisTemplate) {
    redisSetOp = redisTemplate.opsForSet();
    this.redisTemplate = redisTemplate;
  }

  public void saveRefreshToken(String userName, String token) {
    redisSetOp.add(userName, token);
  }

  public Boolean findRefreshToken(String name) {
    ScanOptions options = ScanOptions.scanOptions().match("*").count(1).build();
    Long size = redisSetOp.size(name);
//    Cursor<String> cursor = redisSetOp.scan(name, options);
    if (size == 1) {
      return true;
    } else {
      return false;
    }
  }

  public void setDataExpired(String key, long duration){
    redisTemplate.expire(key, duration, TimeUnit.MILLISECONDS);
  }
}
