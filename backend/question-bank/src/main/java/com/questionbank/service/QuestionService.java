package com.questionbank.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.questionbank.entity.Question;
import com.questionbank.entity.QuestionImage;
import com.questionbank.entity.QuestionType;
import com.questionbank.mapper.QuestionMapper;
import com.questionbank.mapper.QuestionImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 题目服务类
 */
@Service
@Transactional
public class QuestionService {
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private QuestionImageMapper questionImageMapper;
    
    /**
     * 创建题目
     */
    public Question createQuestion(Question question) {
        questionMapper.insert(question);
        return question;
    }
    
    /**
     * 根据ID获取题目
     */
    public Question getQuestionById(Long id) {
        return questionMapper.selectById(id);
    }
    
    /**
     * 获取所有题目（分页）
     */
    public Page<Question> getAllQuestions(Page<Question> page) {
        return questionMapper.selectPage(page, null);
    }
    
    /**
     * 根据类型获取题目
     */
    public List<Question> getQuestionsByType(QuestionType type) {
        return questionMapper.findByType(type);
    }
    
    /**
     * 根据科目获取题目
     */
    public List<Question> getQuestionsBySubject(String subject) {
        return questionMapper.findBySubject(subject);
    }
    
    /**
     * 根据难度等级获取题目
     */
    public List<Question> getQuestionsByDifficulty(Integer difficultyLevel) {
        return questionMapper.findByDifficultyLevel(difficultyLevel);
    }
    
    /**
     * 搜索题目（根据内容）
     */
    public List<Question> searchQuestions(String keyword) {
        return questionMapper.findByContentContainingIgnoreCase(keyword);
    }
    
    /**
     * 更新题目
     */
    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionMapper.selectById(id);
        if (question != null) {
            question.setContent(questionDetails.getContent());
            question.setType(questionDetails.getType());
            question.setCorrectAnswer(questionDetails.getCorrectAnswer());
            question.setOptions(questionDetails.getOptions());
            question.setDifficultyLevel(questionDetails.getDifficultyLevel());
            question.setSubject(questionDetails.getSubject());
            question.setChapter(questionDetails.getChapter());
            questionMapper.updateById(question);
            return question;
        }
        return null;
    }
    
    /**
     * 删除题目
     */
    public boolean deleteQuestion(Long id) {
        Question question = questionMapper.selectById(id);
        if (question != null) {
            // 先删除相关的图片记录
            questionImageMapper.deleteByQuestionId(id);
            // 再删除题目
            questionMapper.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * 为题目添加图片
     */
    public QuestionImage addImageToQuestion(Long questionId, String imagePath, String imageName) {
        Question question = questionMapper.selectById(questionId);
        if (question != null) {
            QuestionImage questionImage = new QuestionImage(questionId, imagePath, imageName);
            questionImageMapper.insert(questionImage);
            return questionImage;
        }
        return null;
    }
    
    /**
     * 获取题目的所有图片
     */
    public List<QuestionImage> getQuestionImages(Long questionId) {
        return questionImageMapper.findByQuestionId(questionId);
    }
    
    /**
     * 批量创建题目
     */
    public List<Question> createQuestions(List<Question> questions) {
        for (Question question : questions) {
            questionMapper.insert(question);
        }
        return questions;
    }
    
    /**
     * 根据源文件获取题目
     */
    public List<Question> getQuestionsBySourceFile(String sourceFile) {
        return questionMapper.findBySourceFile(sourceFile);
    }
    
    /**
     * 统计题目数量
     */
    public long countQuestions() {
        return questionMapper.selectCount(null);
    }
    
    /**
     * 根据类型统计题目数量
     */
    public long countQuestionsByType(QuestionType type) {
        return questionMapper.countByType(type);
    }
}