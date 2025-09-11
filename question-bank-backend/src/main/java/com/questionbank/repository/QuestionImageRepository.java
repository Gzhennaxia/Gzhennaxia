package com.questionbank.repository;

import com.questionbank.entity.QuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目图片数据访问层
 */
@Repository
public interface QuestionImageRepository extends JpaRepository<QuestionImage, Long> {
    
    /**
     * 根据题目ID查找所有图片
     */
    List<QuestionImage> findByQuestionId(Long questionId);
    
    /**
     * 根据题目ID删除所有图片
     */
    void deleteByQuestionId(Long questionId);
    
    /**
     * 根据图片路径查找
     */
    QuestionImage findByImagePath(String imagePath);
    
    /**
     * 统计指定题目的图片数量
     */
    long countByQuestionId(Long questionId);
}