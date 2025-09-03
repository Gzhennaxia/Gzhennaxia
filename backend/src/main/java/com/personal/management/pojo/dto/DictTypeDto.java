package com.personal.management.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Gzhennaxia
 */
@Data
public class DictTypeDto {
    private Long id;
    private String code;
    private String name;
    private String version;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
    private List<DictItemDto> dictItems;
}
