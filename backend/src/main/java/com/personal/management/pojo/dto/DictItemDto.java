package com.personal.management.pojo.dto;

import lombok.Data;

/**
 * @author Gzhennaxia
 */
@Data
public class DictItemDto {
    private Long id;
    private String typeCode;
    private String itemKey;
    private String itemValue;
    private Integer status;
    private Integer sort;
    private String remark;
    private String createdTime;
    private String updatedTime;
}
