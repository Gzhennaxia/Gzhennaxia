package com.questionbank.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 题目图片实体类
 */
@TableName("question_images")
public class QuestionImage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("question_id")
    private Long questionId; // 改为直接存储 questionId，而不是 Question 对象
    
    @TableField("image_path")
    private String imagePath;
    
    @TableField("image_name")
    private String imageName;
    
    @TableField("image_size")
    private Long imageSize;
    
    @TableField("image_type")
    private String imageType;
    
    @TableField("position_in_question")
    private Integer positionInQuestion;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 构造函数
    public QuestionImage() {}
    
    public QuestionImage(Question question, String imagePath, String imageName) {
        this.questionId = question.getId();
        this.imagePath = imagePath;
        this.imageName = imageName;
    }
    
    public QuestionImage(Long questionId, String imagePath, String imageName) {
        this.questionId = questionId;
        this.imagePath = imagePath;
        this.imageName = imageName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    
    public Long getImageSize() { return imageSize; }
    public void setImageSize(Long imageSize) { this.imageSize = imageSize; }
    
    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    
    public Integer getPositionInQuestion() { return positionInQuestion; }
    public void setPositionInQuestion(Integer positionInQuestion) { this.positionInQuestion = positionInQuestion; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}