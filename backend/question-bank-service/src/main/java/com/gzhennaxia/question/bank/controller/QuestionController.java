package com.gzhennaxia.question.bank.controller;

import com.gzhennaxia.question.bank.entity.QuestionType;
import com.gzhennaxia.question.bank.entity.Question;
import com.gzhennaxia.question.bank.entity.QuestionImage;
import com.gzhennaxia.question.bank.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 题目管理控制器
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    /**
     * 创建题目
     */
    @PostMapping
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody Question question) {
        Question createdQuestion = questionService.createQuestion(question);
        return ResponseEntity.ok(createdQuestion);
    }
    
    /**
     * 获取所有题目（分页）
     */
    @GetMapping
    public ResponseEntity<Page<Question>> getAllQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Question> pageRequest = new Page<>(page, size);
        Page<Question> questions = questionService.getAllQuestions(pageRequest);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * 根据ID获取题目
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        if (question != null) {
            return ResponseEntity.ok(question);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 根据类型获取题目
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Question>> getQuestionsByType(@PathVariable QuestionType type) {
        List<Question> questions = questionService.getQuestionsByType(type);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * 根据科目获取题目
     */
    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Question>> getQuestionsBySubject(@PathVariable String subject) {
        List<Question> questions = questionService.getQuestionsBySubject(subject);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * 根据难度获取题目
     */
    @GetMapping("/difficulty/{level}")
    public ResponseEntity<List<Question>> getQuestionsByDifficulty(@PathVariable Integer level) {
        List<Question> questions = questionService.getQuestionsByDifficulty(level);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * 搜索题目
     */
    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestions(@RequestParam String keyword) {
        List<Question> questions = questionService.searchQuestions(keyword);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, 
                                                 @Valid @RequestBody Question questionDetails) {
        Question updatedQuestion = questionService.updateQuestion(id, questionDetails);
        if (updatedQuestion != null) {
            return ResponseEntity.ok(updatedQuestion);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        boolean deleted = questionService.deleteQuestion(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 获取题目的图片
     */
    @GetMapping("/{id}/images")
    public ResponseEntity<List<QuestionImage>> getQuestionImages(@PathVariable Long id) {
        List<QuestionImage> images = questionService.getQuestionImages(id);
        return ResponseEntity.ok(images);
    }
    
    /**
     * 批量创建题目
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Question>> createQuestions(@Valid @RequestBody List<Question> questions) {
        List<Question> createdQuestions = questionService.createQuestions(questions);
        return ResponseEntity.ok(createdQuestions);
    }
    
    /**
     * 获取题目统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getQuestionStats() {
        long totalCount = questionService.countQuestions();
        long singleChoiceCount = questionService.countQuestionsByType(QuestionType.SINGLE_CHOICE);
        long multipleChoiceCount = questionService.countQuestionsByType(QuestionType.MULTIPLE_CHOICE);
        long trueFalseCount = questionService.countQuestionsByType(QuestionType.TRUE_FALSE);
        long fillBlankCount = questionService.countQuestionsByType(QuestionType.FILL_BLANK);
        long shortAnswerCount = questionService.countQuestionsByType(QuestionType.SHORT_ANSWER);
        long essayCount = questionService.countQuestionsByType(QuestionType.ESSAY);
        
        return ResponseEntity.ok(new Object() {
            public final long total = totalCount;
            public final long singleChoice = singleChoiceCount;
            public final long multipleChoice = multipleChoiceCount;
            public final long trueFalse = trueFalseCount;
            public final long fillBlank = fillBlankCount;
            public final long shortAnswer = shortAnswerCount;
            public final long essay = essayCount;
        });
    }
}