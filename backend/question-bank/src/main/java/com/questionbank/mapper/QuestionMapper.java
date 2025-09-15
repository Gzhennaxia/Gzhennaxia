package com.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.questionbank.entity.Question;
import com.questionbank.entity.QuestionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目数据访问层 - MyBatis Mapper
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    
    /**
     * 根据题目类型查找
     */
    @Select("SELECT * FROM questions WHERE type = #{type}")
    List<Question> findByType(@Param("type") QuestionType type);
    
    /**
     * 根据科目查找
     */
    @Select("SELECT * FROM questions WHERE subject = #{subject}")
    List<Question> findBySubject(@Param("subject") String subject);
    
    /**
     * 根据章节查找
     */
    @Select("SELECT * FROM questions WHERE chapter = #{chapter}")
    List<Question> findByChapter(@Param("chapter") String chapter);
    
    /**
     * 根据难度等级查找
     */
    @Select("SELECT * FROM questions WHERE difficulty_level = #{difficultyLevel}")
    List<Question> findByDifficultyLevel(@Param("difficultyLevel") Integer difficultyLevel);
    
    /**
     * 根据源文件查找
     */
    @Select("SELECT * FROM questions WHERE source_file = #{sourceFile}")
    List<Question> findBySourceFile(@Param("sourceFile") String sourceFile);
    
    /**
     * 根据内容搜索（忽略大小写）
     */
    @Select("SELECT * FROM questions WHERE LOWER(content) LIKE LOWER(CONCAT('%', #{keyword}, '%'))")
    List<Question> findByContentContainingIgnoreCase(@Param("keyword") String keyword);
    
    /**
     * 根据科目和章节查找
     */
    @Select("SELECT * FROM questions WHERE subject = #{subject} AND chapter = #{chapter}")
    List<Question> findBySubjectAndChapter(@Param("subject") String subject, @Param("chapter") String chapter);
    
    /**
     * 根据类型和难度等级查找
     */
    @Select("SELECT * FROM questions WHERE type = #{type} AND difficulty_level = #{difficultyLevel}")
    List<Question> findByTypeAndDifficultyLevel(@Param("type") QuestionType type, @Param("difficultyLevel") Integer difficultyLevel);
    
    /**
     * 统计指定类型的题目数量
     */
    @Select("SELECT COUNT(*) FROM questions WHERE type = #{type}")
    long countByType(@Param("type") QuestionType type);
    
    /**
     * 统计指定科目的题目数量
     */
    @Select("SELECT COUNT(*) FROM questions WHERE subject = #{subject}")
    long countBySubject(@Param("subject") String subject);
    
    /**
     * 根据多个条件查询
     */
    @Select("<script>" +
            "SELECT * FROM questions WHERE 1=1 " +
            "<if test='type != null'> AND type = #{type} </if>" +
            "<if test='subject != null'> AND subject = #{subject} </if>" +
            "<if test='chapter != null'> AND chapter = #{chapter} </if>" +
            "<if test='difficultyLevel != null'> AND difficulty_level = #{difficultyLevel} </if>" +
            "</script>")
    List<Question> findByMultipleConditions(@Param("type") QuestionType type,
                                          @Param("subject") String subject,
                                          @Param("chapter") String chapter,
                                          @Param("difficultyLevel") Integer difficultyLevel);
    
    /**
     * 随机获取指定数量的题目
     */
    @Select("SELECT * FROM questions ORDER BY RAND() LIMIT #{count}")
    List<Question> findRandomQuestions(@Param("count") int count);
    
    /**
     * 根据类型随机获取题目
     */
    @Select("SELECT * FROM questions WHERE type = #{type} ORDER BY RAND() LIMIT #{count}")
    List<Question> findRandomQuestionsByType(@Param("type") String type, @Param("count") int count);
}