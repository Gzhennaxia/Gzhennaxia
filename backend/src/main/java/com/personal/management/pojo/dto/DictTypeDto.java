package com.personal.management.pojo.dto;

import lombok.Data;

@Data
public class DictTypeDto {
    private String code;
    private String name;
    private String version;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
}
