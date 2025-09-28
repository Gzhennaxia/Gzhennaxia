package com.gzhennaxia.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 答题记录实体类
 */
@Data
@TableName("answer_record")
public class AnswerRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 试题ID
     */
    private Long questionId;
    
    /**
     * 个人答题答案
     */
    private String userAnswer;
    
    /**
     * 是否答对
     */
    private Boolean isCorrect;
    
    /**
     * 答题时间
     */
    private LocalDateTime answerTime;
    
    /**
     * 是否加入错题本
     */
    private Boolean isWrongBook;
    
    /**
     * 错误次数
     */
    private Integer wrongCount;
    
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