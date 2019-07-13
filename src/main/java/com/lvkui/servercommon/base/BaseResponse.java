package com.lvkui.servercommon.base;

import lombok.Data;

@Data
public class BaseResponse {
  private boolean success;
  private int code;
  private String message;

  public static final BaseResponse SUCCESS = new BaseResponse(true, 0, "");
  public static final BaseResponse INTERNAL_ERROR = new BaseResponse(false, 500, "internal error");
  public static final BaseResponse PARAM_ERROR = new BaseResponse(false, 400, "invalid params");
  public static final BaseResponse NOT_LOGIN = new BaseResponse(false, 401, "用户未登录");
  public static final BaseResponse NO_AUTH = new BaseResponse(false, 402, "no auth");
  public static final BaseResponse INVALID_USER_PWD_ERR = new BaseResponse(false, 403, "user name or password incorrect");
  public static final BaseResponse VERIFY_CODE_FAIL = new BaseResponse(false, 403, "verification code fail");


  public BaseResponse() {}

  public BaseResponse(boolean success, int code, String message) {
    this.success = success;
    this.code = code;
    this.message = message;
  }

  public BaseResponse(BaseResponse response) {
    this.success = response.success;
    this.code = response.code;
    this.message = response.message;
  }

}
