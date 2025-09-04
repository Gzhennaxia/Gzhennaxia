package com.personal.management.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("dict_item")
public class DictItem {
    private Long id;
    
    @TableField("dict_code")
    private String typeCode;
    
    @TableField("item_code")
    private String itemKey;
    
    @TableField("item_name")
    private String itemValue;
    
    private Integer sort;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
    private Integer deleted = 0;
}