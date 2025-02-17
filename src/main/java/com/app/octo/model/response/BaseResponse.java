package com.app.octo.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class BaseResponse {
  private Long id;
  private Date createdDate;
  private Date updatedDate;
}
