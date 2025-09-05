package com.personal.management.pojo.dto;

import lombok.Data;

/**
 * @author Gzhennaxia
 */
@Data
public class DictItemDto {
    private Long id;
    private String dictCode;
    private String itemCode;
    private String itemName;
    private Integer status;
    private Integer sort;
    private String remark;
    private String createdTime;
    private String updatedTime;
}
