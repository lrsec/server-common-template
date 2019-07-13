package com.lvkui.servercommon.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResponseException extends RuntimeException {
  private int code;
  private String message;

  public static final ResponseException NO_AUTH_EXCEPTION = new ResponseException("用户未登录", 401, "用户未登录");

  public ResponseException() {
    super();
  }

  public ResponseException(Exception e, int code, String message) {
    super(e);
    this.code = code;
    this.message = message;
  }

  public ResponseException(String msg, int code, String message) {
    super(msg);
    this.code = code;
    this.message = message;
  }

}
