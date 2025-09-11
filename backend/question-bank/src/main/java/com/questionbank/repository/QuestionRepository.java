package com.questionbank.repository;

import com.questionbank.entity.Question;
import com.questionbank.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目数据访问层
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * 根据题目类型查找
     */
    List<Question> findByType(QuestionType type);
    
    /**
     * 根据科目查找
     */
    List<Question> findBySubject(String subject);
    
    /**
     * 根据章节查找
     */
    List<Question> findByChapter(String chapter);
    
    /**
     * 根据难度等级查找
     */
    List<Question> findByDifficultyLevel(Integer difficultyLevel);
    
    /**
     * 根据源文件查找
     */
    List<Question> findBySourceFile(String sourceFile);
    
    /**
     * 根据内容搜索（忽略大小写）
     */
    List<Question> findByContentContainingIgnoreCase(String keyword);
    
    /**
     * 根据科目和章节查找
     */
    List<Question> findBySubjectAndChapter(String subject, String chapter);
    
    /**
     * 根据类型和难度等级查找
     */
    List<Question> findByTypeAndDifficultyLevel(QuestionType type, Integer difficultyLevel);
    
    /**
     * 统计指定类型的题目数量
     */
    long countByType(QuestionType type);
    
    /**
     * 统计指定科目的题目数量
     */
    long countBySubject(String subject);
    
    /**
     * 根据多个条件查询
     */
    @Query("SELECT q FROM Question q WHERE " +
           "(:type IS NULL OR q.type = :type) AND " +
           "(:subject IS NULL OR q.subject = :subject) AND " +
           "(:chapter IS NULL OR q.chapter = :chapter) AND " +
           "(:difficultyLevel IS NULL OR q.difficultyLevel = :difficultyLevel)")
    List<Question> findByMultipleConditions(@Param("type") QuestionType type,
                                          @Param("subject") String subject,
                                          @Param("chapter") String chapter,
                                          @Param("difficultyLevel") Integer difficultyLevel);
    
    /**
     * 随机获取指定数量的题目
     */
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("count") int count);
    
    /**
     * 根据类型随机获取题目
     */
    @Query(value = "SELECT * FROM questions WHERE type = :type ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Question> findRandomQuestionsByType(@Param("type") String type, @Param("count") int count);
}