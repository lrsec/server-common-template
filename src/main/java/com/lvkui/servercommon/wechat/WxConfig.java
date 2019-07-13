package com.lvkui.servercommon.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WxConfig {
  private String miniAppid;
  private String miniSecret;
  private String miniSessionKeyPrefix;

  private String mchid;
  private String mchkey;
  private String keyPath;
}
