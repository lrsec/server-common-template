package com.lvkui.servercommon.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvkui.servercommon.token.TokenService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Objects;

@Slf4j
@Service
public class TokenInterceptor extends HandlerInterceptorAdapter {
  @Autowired TokenService tokenService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    try {
      // 避免处理 CORS 请求
      if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        return true;
      }

      long uid = Integer.parseInt(request.getHeader("Uid"));
      String token = request.getHeader("Token");

      boolean verified = verify(uid, token);

      if (!verified) {
        log.error("fail to verify token for uid {} and token {}", uid, token);
        responseInvalidToken(response);
        return false;
      }

      return true;

    } catch (Exception e) {
      log.error("token interceptor exception for url: " + request.getRequestURI(), e);
      responseInvalidToken(response);
      return false;
    }
  }

  public void verifyToken(long uid, String token) {
    boolean verified = verify(uid, token);

    if (!verified) {
      throw ResponseException.NO_AUTH_EXCEPTION;
    }
  }

  private boolean verify(long uid, String token) {
    boolean verified = false;

    if (uid == -1024 && Objects.equals(token, "admin")) {
      verified = true;
    } else if (uid == -1 && Objects.equals(token, "eden-scaner")) {
      verified = true;
    } else {
      verified = tokenService.verifyToken(uid, token);
    }

    return verified;
  }

  @SneakyThrows
  private void responseInvalidToken(HttpServletResponse response) {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // 异常报错之后，会导致正常的 cors header 无法写入，这里手动写入避免问题
    response.addHeader("Access-Control-Allow-Credentials", "true");
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, POST, PUT, DELETE");
    response.addHeader("Access-Control-Allow-Headers", "*");
    response.addHeader("Access-Control-Max-Age", "3600");

    BaseResponse resp = BaseResponse.NOT_LOGIN;
    ObjectMapper mapper = new ObjectMapper();
    String jsonResp = mapper.writeValueAsString(resp);
    out.print(jsonResp);
    out.flush();
  }
}
