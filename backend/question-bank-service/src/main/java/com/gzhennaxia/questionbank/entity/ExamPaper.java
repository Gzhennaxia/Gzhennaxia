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
 * 试卷实体类
 */
@Data
@TableName(value = "exam_paper", autoResultMap = true)
public class ExamPaper {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 试卷名称
     */
    private String paperName;
    
    /**
     * 试卷包含的试题ID列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> questionIds;
    
    /**
     * 总题数
     */
    private Integer totalQuestion;
    
    /**
     * 完成时间
     */
    private LocalDateTime finishTime;
    
    /**
     * 用时（单位：秒）
     */
    private Integer usedTime;
    
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