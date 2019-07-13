package com.lvkui.servercommon.wechat;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Lazy
@Service
public class WxMiniAppServiceManager {
  private final WxMaService wxMaService = new WxMaServiceImpl();
  @Autowired WxConfig wxConfig;

  @PostConstruct
  public void init() {
    WxMaInMemoryConfig config = new WxMaInMemoryConfig();
    config.setAppid(this.wxConfig.getMiniAppid());
    config.setSecret(this.wxConfig.getMiniSecret());
    this.wxMaService.setWxMaConfig(config);
  }

  public WxMaService getService() {
    return this.wxMaService;
  }
}
