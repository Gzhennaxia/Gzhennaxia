package com.gzhennaxia.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试题实体类
 */
@Data
@TableName(value = "question", autoResultMap = true)
public class Question {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 题型（single_choice/multiple_choice/judge/fill/essay）
     */
    private String questionType;
    
    /**
     * 题干内容
     */
    private String content;
    
    /**
     * 选项（选择题专用，存储选项列表，如 ["A.xxx","B.xxx"]）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;
    
    /**
     * 答案（选择题用 A/B/C/D，填空题用具体文本，判断题用 true/false）
     */
    private String answer;
    
    /**
     * 解析内容
     */
    private String analysis;
    
    /**
     * 难度（easy/medium/hard）
     */
    private String difficulty;
    
    /**
     * 试题来源（如 "真题""模拟题"）
     */
    private String source;
    
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