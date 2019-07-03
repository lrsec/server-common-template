package com.lvkui.servercommon.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class ContentsResponse<T> extends BaseResponse {
  private List<T> contents;
  private long total;

  public ContentsResponse(List<T> contents, long total) {
    super(BaseResponse.SUCCESS);
    this.contents = contents;
    this.total = total;
  }
}
