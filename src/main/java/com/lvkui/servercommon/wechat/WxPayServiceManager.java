package com.lvkui.servercommon.wechat;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Lazy
@Service
public class WxPayServiceManager {
  private final WxPayService wxPayService = new WxPayServiceImpl();
  @Autowired
  WxConfig wxConfig;

  @PostConstruct
  public void init() {
    // wechat pay
    WxPayConfig payConfig = new WxPayConfig();
    payConfig.setAppId(StringUtils.trimToNull(wxConfig.getMiniAppid()));
    payConfig.setMchId(StringUtils.trimToNull(wxConfig.getMchid()));
    payConfig.setMchKey(StringUtils.trimToNull(wxConfig.getMchkey()));
    payConfig.setKeyPath(StringUtils.trimToNull(wxConfig.getKeyPath()));
    // 可以指定是否使用沙箱环境
    payConfig.setUseSandboxEnv(false);
    wxPayService.setConfig(payConfig);
  }

  public WxPayService getService() {
    return this.wxPayService;
  }
}
