package com.lvkui.servercommon.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BaseController {

  @ExceptionHandler(ResponseException.class)
  public ResponseEntity<BaseResponse> handleResponseException(ResponseException ex) {
    log.error("controller exception: ", ex);

    BaseResponse resp = new BaseResponse(false, ex.getCode(), ex.getMessage());
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  @ExceptionHandler
  public ResponseEntity<BaseResponse> handle(Exception ex) {
    log.error("controller exception: ", ex);

    BaseResponse resp = BaseResponse.INTERNAL_ERROR;
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

}
