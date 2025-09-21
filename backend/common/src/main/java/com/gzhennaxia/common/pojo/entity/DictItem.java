package com.gzhennaxia.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dict_item")
public class DictItem {
    private Long id;
    private String dictCode;
    private String itemCode;
    private String itemName;
    private Integer sort;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
    private Integer deleted = 0;
}