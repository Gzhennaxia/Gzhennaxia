package com.gzhennaxia.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试题-标签关联实体类
 */
@Data
@TableName("question_tag_rel")
public class QuestionTagRel {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 试题ID
     */
    private Long questionId;
    
    /**
     * 标签ID
     */
    private Long tagId;
    
    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status;
    
    /**
     * 删除标记：0=未删除，1=已删除
     */
    private Integer deleted;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}