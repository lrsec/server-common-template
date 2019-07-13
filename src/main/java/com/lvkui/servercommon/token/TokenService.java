package com.lvkui.servercommon.token;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.Random;

@Slf4j
@Service
public class TokenService {
  @Autowired private TokenServiceConfig tokenServiceConfig;
  @Autowired private StringRedisTemplate redisTemplate;

  private Random random = new Random();

  @SneakyThrows
  public String updateToken(long uid) {

    long nowMill = System.currentTimeMillis();

    String content =
        tokenServiceConfig.getKeyPrefix()
            + String.valueOf(uid)
            + "_"
            + String.valueOf(random.nextInt(100))
            + "_"
            + String.valueOf(nowMill);

    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(content.getBytes());
    byte[] digest = md.digest();
    String token = DatatypeConverter.printHexBinary(digest).toLowerCase();

    String key = getTokenKey(uid);
    long expireSeconds = (nowMill / 1000L + tokenServiceConfig.getExpireMins() * 60);

    Boolean success = redisTemplate.opsForZSet().add(key, token, expireSeconds);
    if (!success) {
      throw new RuntimeException(String.format("update token fail for uid: %d", uid));
    }

    shrinkToken(key);

    return token;
  }

  public void deleteToken(long uid, String token) {
    String key = getTokenKey(uid);
    long removed = redisTemplate.opsForZSet().remove(key, token);
    if (removed <= 0) {
      log.warn(String.format("remove token %s for uid %d return %d", token, uid, removed));
    }
  }

  public void deleteToken(long uid) {
    String key = getTokenKey(uid);
    Boolean success = redisTemplate.delete(key);
    if (!success) {
      log.warn(String.format("delete token for uid %d fail", uid));
    }
  }

  public boolean verifyToken(long uid, String token) {
    if (token == null) {
      return false;
    }

    String key = getTokenKey(uid);
    Double score = redisTemplate.opsForZSet().score(key, token);

    long now = System.currentTimeMillis() / 1000L;
    if (score == null || score < now) {
      return false;
    }

    long expireSeconds = now + tokenServiceConfig.getExpireMins() * 60;

    redisTemplate.opsForZSet().add(key, token, expireSeconds);

    shrinkToken(key);

    return true;
  }

  @Async
  public void shrinkToken(String key) {
    try {
      redisTemplate.opsForZSet().removeRange(key, 0, -(tokenServiceConfig.getBothLoginCount() + 1));
    } catch (Exception e) {
      log.error(String.format("shrink token key %s exception", key), e);
    }
  }

  private String getTokenKey(long uid) {
    return tokenServiceConfig.getKeyPrefix() + "_" + String.valueOf(uid);
  }
}
