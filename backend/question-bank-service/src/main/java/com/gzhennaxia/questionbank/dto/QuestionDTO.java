package com.gzhennaxia.questionbank.dto;

import lombok.Data;

import java.util.List;

/**
 * 试题数据传输对象
 */
@Data
public class QuestionDTO {
    
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
     * 关联的标签ID列表
     */
    private List<Long> tagIds;
}