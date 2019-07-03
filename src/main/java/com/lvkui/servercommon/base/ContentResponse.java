package com.lvkui.servercommon.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContentResponse<T> extends BaseResponse {
  private T content;

  public ContentResponse(T content) {
    super(BaseResponse.SUCCESS);
    this.content = content;
  }
}
