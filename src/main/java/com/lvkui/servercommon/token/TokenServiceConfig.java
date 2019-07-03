package com.lvkui.servercommon.token;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "token.service")
public class TokenServiceConfig {
  private int bothLoginCount;
  private int expireMins;
  private String keyPrefix;
}
