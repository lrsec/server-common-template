package com.lvkui.servercommon.push;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaUniformMessage;
import com.lvkui.servercommon.utils.Consts;
import com.lvkui.servercommon.wechat.WxMiniAppServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class WeappPushService {
  public static final char separator = ',';

  @Autowired private StringRedisTemplate redisTemplate;
  @Autowired private WxMiniAppServiceManager wxMiniAppServiceManager;

  // 支付可以用三条
  public void savePrepayId(long uid, String prepayId) {
    saveFormId(uid, prepayId);
    saveFormId(uid, prepayId);
    saveFormId(uid, prepayId);
  }

  public void saveFormId(long uid, String formId) {
    String key = getKey(uid);

    long nowSecond = System.currentTimeMillis() / 1000;

    String value = formId + separator + nowSecond;

    redisTemplate.opsForList().rightPush(key, value);
  }

  public String getValidFormid(long uid) {
    String key = getKey(uid);

    String formId = null;
    String value = redisTemplate.opsForList().leftPop(key);
    while (value != null) {
      try {
        String[] contents = Strings.split(value, separator);

        long second = Long.parseLong(contents[1]);
        long nowSec = System.currentTimeMillis() / 1000;

        // 向前推5分钟，避免使用过期数据
        if ((nowSec - 5 * 60 - second) / (24 * 3600) < 7) {
          formId = contents[0];
          break;
        } else {
          value = redisTemplate.opsForList().leftPop(key);
          continue;
        }

      } catch (Exception e) {
        log.error("Exception in get valid formid for " + uid, e);
      }
    }

    return formId;
  }

  @Async
  public void shrinkList(String key) {
    try {
      String value = redisTemplate.opsForList().index(key, 0);
      while (value != null) {
        String[] contents = Strings.split(value, separator);

        long second = Long.parseLong(contents[1]);
        long nowSec = System.currentTimeMillis() / 1000;

        if ((nowSec - 5 * 60 - second) / (24 * 3600) >= 7) {
          redisTemplate.opsForList().leftPop(key);
        } else {
          break;
        }
      }
    } catch (Exception e) {
      log.error("exception in shrinking mini formid list for " + key, e);
    }
  }

  @Async
  public void sendPayedPush(
      long uid, String openId, long topicId, int price, LocalDateTime createTime) {
    try {
      String formId = getValidFormid(uid);
      if (formId == null) {
        log.error("uid {} send payed_push fail: do not have valid form id", uid);
      }

      WxMaMsgService msgService = wxMiniAppServiceManager.getService().getMsgService();

      List<WxMaTemplateData> datas = new LinkedList<>();
      datas.add(new WxMaTemplateData("keyword1", "彬彬有法问题咨询"));
      datas.add(new WxMaTemplateData("keyword2", price > 0 ? (price / 100.0) + "元" : "免费"));
      datas.add(new WxMaTemplateData("keyword3", "已提交"));
      datas.add(new WxMaTemplateData("keyword4", createTime.format(Consts.NORMAL_FORMAT)));
      datas.add(
          new WxMaTemplateData("keyword5", createTime.plusHours(24).format(Consts.NORMAL_FORMAT)));

      WxMaUniformMessage msg = new WxMaUniformMessage();
      msg.setToUser(openId);
      msg.setTemplateId(Consts.PUSH_ID_PAYED);
      msg.setPage("/pages/consult/session?topicId=" + topicId + "&showReturnHome=true");
      msg.setFormId(formId);
      msg.setData(datas);

      msgService.sendUniformMsg(msg);

    } catch (Exception e) {
      log.error(
          "send payed push for uid: " + uid + " topic: " + topicId + "fail with exception", e);
    }
  }

  @Async
  public void sendNewConsultPush(
      long acceptorId,
      String openId,
      long topicId,
      String askerName,
      int price,
      String title,
      LocalDateTime createTime) {
    try {
      String formId = getValidFormid(acceptorId);
      if (formId == null) {
        log.error(
            "acceptorId {} send new consult push fail: do not have valid form id", acceptorId);
      }

      WxMaMsgService msgService = wxMiniAppServiceManager.getService().getMsgService();

      createTime = createTime.plusHours(24);
      String answerTime =
          createTime.getYear()
              + "年"
              + createTime.getMonth().getValue()
              + "月"
              + createTime.getDayOfMonth()
              + "日 "
              + createTime.getHour()
              + ":"
              + createTime.getMinute()
              + "前";

      List<WxMaTemplateData> datas = new LinkedList<>();
      datas.add(new WxMaTemplateData("keyword1", askerName));
      datas.add(new WxMaTemplateData("keyword2", price > 0 ? (price / 100.0) + "元" : "免费"));
      datas.add(new WxMaTemplateData("keyword3", title));
      datas.add(new WxMaTemplateData("keyword4", answerTime));

      WxMaUniformMessage msg = new WxMaUniformMessage();
      msg.setToUser(openId);
      msg.setTemplateId(Consts.PUSH_ID_CONSULT);
      msg.setPage("/pages/consult/session?topicId=" + topicId + "&showReturnHome=true");
      msg.setFormId(formId);
      msg.setData(datas);

      msgService.sendUniformMsg(msg);

    } catch (Exception e) {
      log.error(
          "send new consult push for acceptor: "
              + acceptorId
              + " for topic id: "
              + topicId
              + " fail with exception",
          e);
    }
  }

  @Async
  public void sendRefundPush(
      long uid, String openId, long topicId, int price, String reason, LocalDateTime refundTime) {
    try {
      String formId = getValidFormid(uid);
      if (formId == null) {
        log.error("uid {} send refund consult push fail: do not have valid form id", uid);
      }

      WxMaMsgService msgService = wxMiniAppServiceManager.getService().getMsgService();

      List<WxMaTemplateData> datas = new LinkedList<>();
      datas.add(new WxMaTemplateData("keyword1", price > 0 ? (price / 100.0) + "元" : "免费"));
      datas.add(new WxMaTemplateData("keyword2", reason));
      datas.add(new WxMaTemplateData("keyword3", refundTime.format(Consts.NORMAL_FORMAT)));

      WxMaUniformMessage msg = new WxMaUniformMessage();
      msg.setToUser(openId);
      msg.setTemplateId(Consts.PUSH_ID_REFUND);
      msg.setPage("/pages/consult/session?topicId=" + topicId + "&showReturnHome=true");
      msg.setFormId(formId);
      msg.setData(datas);

      msgService.sendUniformMsg(msg);

    } catch (Exception e) {
      log.error(
          "send refund push for acceptor: "
              + uid
              + " for topic id: "
              + topicId
              + " fail with exception",
          e);
    }
  }

  @Async
  public void sendAnswerConsultPush(
      long uid,
      String openId,
      long topicId,
      String title,
      String answerName,
      String content,
      LocalDateTime answerTime) {
    try {
      String formId = getValidFormid(uid);
      if (formId == null) {
        log.error("uid {} send answer consult push fail: do not have valid form id", uid);
      }

      WxMaMsgService msgService = wxMiniAppServiceManager.getService().getMsgService();

      List<WxMaTemplateData> datas = new LinkedList<>();
      datas.add(new WxMaTemplateData("keyword1", title));
      datas.add(new WxMaTemplateData("keyword2", answerName));
      datas.add(new WxMaTemplateData("keyword3", content));
      datas.add(new WxMaTemplateData("keyword4", answerTime.format(Consts.NORMAL_FORMAT)));

      WxMaUniformMessage msg = new WxMaUniformMessage();
      msg.setToUser(openId);
      msg.setTemplateId(Consts.PUSH_ID_CONSULT_ANSWER);
      msg.setPage("/pages/consult/session?topicId=" + topicId + "&showReturnHome=true");
      msg.setFormId(formId);
      msg.setData(datas);

      msgService.sendUniformMsg(msg);

    } catch (Exception e) {
      log.error(
          "send answer consult push for uid: "
              + uid
              + " for topic id: "
              + topicId
              + " fail with exception",
          e);
    }
  }

  private String getKey(long uid) {
    return "mini_formid_" + uid;
  }
}
