package com.personal.management.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dict_type")
public class DictType {
    private Long id;
    private String code;
    private String name;
    private String version;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
    private Boolean deleted = false;
}