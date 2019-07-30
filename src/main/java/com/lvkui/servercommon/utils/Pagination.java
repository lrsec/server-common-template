package com.lvkui.servercommon.utils;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// Vuetify page pagination and sorting help class
@Data
public class Pagination {
  private int page; // 页数,从1开始
  private int rowsPerPage; // 每页个数
  private String sortBy; // 排序位
  private boolean descending; // 是否倒序

  public PageRequest createPageRequest() {

    int rowsPerPage = this.rowsPerPage == -1 ? Integer.MAX_VALUE : this.rowsPerPage;

    if (this.sortBy != null) {
      Sort sort = new Sort(this.descending ? Sort.Direction.DESC : Sort.Direction.ASC, this.sortBy);
      return PageRequest.of(this.page - 1, rowsPerPage, sort);
    } else {
      return PageRequest.of(this.page - 1, rowsPerPage);
    }
  }

  public PageRequest createPageRequest(String sortName, boolean isDesc) {
    int rowsPerPage = this.rowsPerPage == -1 ? Integer.MAX_VALUE : this.rowsPerPage;

    Sort sort = new Sort(isDesc ? Sort.Direction.DESC : Sort.Direction.ASC, sortName);

    if (this.sortBy != null) {
      sort =
          new Sort(this.descending ? Sort.Direction.DESC : Sort.Direction.ASC, this.sortBy)
              .and(sort);
    }

    return PageRequest.of(this.page - 1, rowsPerPage, sort);
  }
}
