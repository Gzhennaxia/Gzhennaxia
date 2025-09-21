package com.gzhennaxia.common.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Gzhennaxia
 */
@Data
public class DictDto {
    private Long id;
    private String dictCode;
    private String dictName;
    private String version;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
    private List<DictItemDto> dictItems;
}
