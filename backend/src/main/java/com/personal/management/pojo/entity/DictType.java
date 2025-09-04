package com.personal.management.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("dict")
public class DictType {
    private Long id;
    
    @TableField("dict_code")
    private String code;
    
    @TableField("dict_name")
    private String name;
    
    private String version;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
    private Integer deleted = 0;
}