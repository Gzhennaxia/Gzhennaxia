package com.questionbank.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 题目图片实体类
 */
@Entity
@Table(name = "question_images")
public class QuestionImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
    
    @Column(name = "image_path", nullable = false)
    private String imagePath;
    
    @Column(name = "image_name")
    private String imageName;
    
    @Column(name = "image_size")
    private Long imageSize;
    
    @Column(name = "image_type")
    private String imageType;
    
    @Column(name = "position_in_question")
    private Integer positionInQuestion;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // 构造函数
    public QuestionImage() {}
    
    public QuestionImage(Question question, String imagePath, String imageName) {
        this.question = question;
        this.imagePath = imagePath;
        this.imageName = imageName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    
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