package com.gzhennaxia.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识点标签实体类
 */
@Data
@TableName("knowledge_tag")
public class KnowledgeTag {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 标签名称（如 "数学 - 函数""逻辑 - 削弱论证"）
     */
    private String tagName;
    
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