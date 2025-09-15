package com.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.questionbank.entity.QuestionImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目图片数据访问层 - MyBatis Mapper
 */
@Mapper
public interface QuestionImageMapper extends BaseMapper<QuestionImage> {
    
    /**
     * 根据题目ID查找所有图片
     */
    @Select("SELECT * FROM question_images WHERE question_id = #{questionId}")
    List<QuestionImage> findByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 根据题目ID删除所有图片
     */
    @Delete("DELETE FROM question_images WHERE question_id = #{questionId}")
    void deleteByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 根据图片路径查找
     */
    @Select("SELECT * FROM question_images WHERE image_path = #{imagePath}")
    QuestionImage findByImagePath(@Param("imagePath") String imagePath);
    
    /**
     * 统计指定题目的图片数量
     */
    @Select("SELECT COUNT(*) FROM question_images WHERE question_id = #{questionId}")
    long countByQuestionId(@Param("questionId") Long questionId);
}