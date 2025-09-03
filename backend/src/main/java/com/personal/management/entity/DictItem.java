package com.personal.management.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dict_item")
public class DictItem {
    private Long id;
    private String typeCode;
    private String itemKey;
    private String itemValue;
    private Integer sort;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
}