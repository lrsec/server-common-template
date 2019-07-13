package com.lvkui.servercommon.wechat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Lazy
@Service
public class WxStorageService {
  @Autowired
  StringRedisTemplate redisTemplate;
  @Autowired
  WxConfig config;

  public void saveSessionKey(int uid, String session) {
    String key = getSessionStorageKey(uid);
    // 4 分钟有效期
    redisTemplate.opsForValue().set(key, session, 4, TimeUnit.MINUTES);
  }

  public String getSessionKey(int uid) {
    String key = getSessionStorageKey(uid);
    String value = redisTemplate.opsForValue().get(key);
    return value;
  }

  private String getSessionStorageKey(int uid) {
    return config.getMiniSessionKeyPrefix() + "_" + uid;
  }
}
