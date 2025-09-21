package com.gzhennaxia.question.bank.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目实体类
 */
@TableName("questions")
public class Question {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotBlank(message = "题目内容不能为空")
    private String content;
    
    @NotNull(message = "题目类型不能为空")
    private QuestionType type;
    
    @TableField("correct_answer")
    private String correctAnswer;
    
    // 注意：MyBatis Plus 不直接支持 List<String>，需要使用 TypeHandler 或者改为 JSON 字符串
    private String options; // 改为 JSON 字符串存储
    
    @TableField("difficulty_level")
    private Integer difficultyLevel;
    
    private String subject;
    
    private String chapter;
    
    @TableField("source_file")
    private String sourceFile;
    
    @TableField("page_number")
    private Integer pageNumber;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Question() {}
    
    public Question(String content, QuestionType type) {
        this.content = content;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    
    // 辅助方法：将 List<String> 转换为 JSON 字符串
    public void setOptionsList(List<String> optionsList) {
        if (optionsList != null && !optionsList.isEmpty()) {
            // 简单的 JSON 格式，实际项目中建议使用 Jackson 或 Gson
            this.options = String.join(",", optionsList);
        } else {
            this.options = null;
        }
    }
    
    // 辅助方法：将 JSON 字符串转换为 List<String>
    public List<String> getOptionsList() {
        if (options != null && !options.trim().isEmpty()) {
            return List.of(options.split(","));
        }
        return List.of();
    }
    
    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getChapter() { return chapter; }
    public void setChapter(String chapter) { this.chapter = chapter; }
    
    public String getSourceFile() { return sourceFile; }
    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }
    
    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}