package com.questionbank.service;

import com.questionbank.entity.Question;
import com.questionbank.entity.QuestionImage;
import com.questionbank.entity.QuestionType;
import com.questionbank.repository.QuestionRepository;
import com.questionbank.repository.QuestionImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 题目服务类
 */
@Service
@Transactional
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionImageRepository questionImageRepository;
    
    /**
     * 创建题目
     */
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    /**
     * 根据ID获取题目
     */
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }
    
    /**
     * 获取所有题目（分页）
     */
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }
    
    /**
     * 根据类型获取题目
     */
    public List<Question> getQuestionsByType(QuestionType type) {
        return questionRepository.findByType(type);
    }
    
    /**
     * 根据科目获取题目
     */
    public List<Question> getQuestionsBySubject(String subject) {
        return questionRepository.findBySubject(subject);
    }
    
    /**
     * 根据难度等级获取题目
     */
    public List<Question> getQuestionsByDifficulty(Integer difficultyLevel) {
        return questionRepository.findByDifficultyLevel(difficultyLevel);
    }
    
    /**
     * 搜索题目（根据内容）
     */
    public List<Question> searchQuestions(String keyword) {
        return questionRepository.findByContentContainingIgnoreCase(keyword);
    }
    
    /**
     * 更新题目
     */
    public Question updateQuestion(Long id, Question questionDetails) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            question.setContent(questionDetails.getContent());
            question.setType(questionDetails.getType());
            question.setCorrectAnswer(questionDetails.getCorrectAnswer());
            question.setOptions(questionDetails.getOptions());
            question.setDifficultyLevel(questionDetails.getDifficultyLevel());
            question.setSubject(questionDetails.getSubject());
            question.setChapter(questionDetails.getChapter());
            return questionRepository.save(question);
        }
        return null;
    }
    
    /**
     * 删除题目
     */
    public boolean deleteQuestion(Long id) {
        if (questionRepository.existsById(id)) {
            // 先删除相关的图片记录
            questionImageRepository.deleteByQuestionId(id);
            // 再删除题目
            questionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * 为题目添加图片
     */
    public QuestionImage addImageToQuestion(Long questionId, String imagePath, String imageName) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            QuestionImage questionImage = new QuestionImage(question, imagePath, imageName);
            return questionImageRepository.save(questionImage);
        }
        return null;
    }
    
    /**
     * 获取题目的所有图片
     */
    public List<QuestionImage> getQuestionImages(Long questionId) {
        return questionImageRepository.findByQuestionId(questionId);
    }
    
    /**
     * 批量创建题目
     */
    public List<Question> createQuestions(List<Question> questions) {
        return questionRepository.saveAll(questions);
    }
    
    /**
     * 根据源文件获取题目
     */
    public List<Question> getQuestionsBySourceFile(String sourceFile) {
        return questionRepository.findBySourceFile(sourceFile);
    }
    
    /**
     * 统计题目数量
     */
    public long countQuestions() {
        return questionRepository.count();
    }
    
    /**
     * 根据类型统计题目数量
     */
    public long countQuestionsByType(QuestionType type) {
        return questionRepository.countByType(type);
    }
}