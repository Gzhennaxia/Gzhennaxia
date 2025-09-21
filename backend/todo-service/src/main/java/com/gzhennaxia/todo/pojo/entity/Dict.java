package com.gzhennaxia.todo.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("DICT")
public class Dict {
    private Long id;
    private String dictCode;
    private String dictName;
    private String version;
    private Integer status;
    private String remark;
    private String createdTime;
    private String updatedTime;
    private Integer deleted = 0;
}