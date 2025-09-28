package com.gzhennaxia.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzhennaxia.questionbank.dto.QuestionDTO;
import com.gzhennaxia.questionbank.entity.Question;
import com.gzhennaxia.questionbank.entity.QuestionTagRel;
import com.gzhennaxia.questionbank.mapper.QuestionMapper;
import com.gzhennaxia.questionbank.mapper.QuestionTagRelMapper;
import com.gzhennaxia.questionbank.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 试题服务实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private QuestionTagRelMapper questionTagRelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addQuestion(QuestionDTO questionDTO) {
        // 转换DTO为实体
        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);
        
        // 设置默认值
        question.setStatus(1);
        question.setDeleted(0);
        question.setCreatedTime(LocalDateTime.now());
        question.setUpdatedTime(LocalDateTime.now());
        
        // 保存试题
        questionMapper.insert(question);
        
        // 保存试题与标签的关联关系
        if (questionDTO.getTagIds() != null && !questionDTO.getTagIds().isEmpty()) {
            saveQuestionTagRels(question.getId(), questionDTO.getTagIds());
        }
        
        return question.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> importQuestions(MultipartFile file, List<Long> tagIds) {
        // 实际项目中需要使用EasyExcel等工具解析Excel文件
        // 这里简化处理，仅返回示例结果
        Map<String, Integer> result = new HashMap<>();
        result.put("successCount", 0);
        result.put("failCount", 0);
        return result;
    }

    @Override
    public Map<String, Object> listQuestions(Long tagId, String difficulty, String questionType, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getDeleted, 0);
        
        // 添加筛选条件
        if (difficulty != null && !difficulty.isEmpty()) {
            queryWrapper.eq(Question::getDifficulty, difficulty);
        }
        
        if (questionType != null && !questionType.isEmpty()) {
            queryWrapper.eq(Question::getQuestionType, questionType);
        }
        
        // 如果有标签ID，需要先查询关联表获取试题ID列表
        if (tagId != null) {
            LambdaQueryWrapper<QuestionTagRel> relWrapper = new LambdaQueryWrapper<>();
            relWrapper.eq(QuestionTagRel::getTagId, tagId);
            relWrapper.eq(QuestionTagRel::getDeleted, 0);
            List<QuestionTagRel> rels = questionTagRelMapper.selectList(relWrapper);
            
            if (rels.isEmpty()) {
                // 没有关联的试题，返回空结果
                Map<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("total", 0);
                emptyResult.put("list", new ArrayList<>());
                emptyResult.put("pageNum", pageNum);
                emptyResult.put("pageSize", pageSize);
                return emptyResult;
            }
            
            List<Long> questionIds = rels.stream().map(QuestionTagRel::getQuestionId).toList();
            queryWrapper.in(Question::getId, questionIds);
        }
        
        // 分页查询
        Page<Question> page = new Page<>(pageNum, pageSize);
        Page<Question> resultPage = questionMapper.selectPage(page, queryWrapper);
        
        // 转换结果
        List<QuestionDTO> questionDTOs = resultPage.getRecords().stream().map(this::convertToDTO).toList();
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", resultPage.getTotal());
        result.put("list", questionDTOs);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuestion(QuestionDTO questionDTO) {
        // 检查试题是否存在
        Question existingQuestion = questionMapper.selectById(questionDTO.getId());
        if (existingQuestion == null || existingQuestion.getDeleted() == 1) {
            throw new RuntimeException("试题不存在");
        }
        
        // 更新试题信息
        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);
        question.setUpdatedTime(LocalDateTime.now());
        
        questionMapper.updateById(question);
        
        // 更新标签关联关系
        if (questionDTO.getTagIds() != null) {
            // 删除旧的关联关系
            LambdaQueryWrapper<QuestionTagRel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(QuestionTagRel::getQuestionId, question.getId());
            questionTagRelMapper.delete(wrapper);
            
            // 添加新的关联关系
            if (!questionDTO.getTagIds().isEmpty()) {
                saveQuestionTagRels(question.getId(), questionDTO.getTagIds());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long id) {
        // 软删除试题
        Question question = new Question();
        question.setId(id);
        question.setDeleted(1);
        question.setUpdatedTime(LocalDateTime.now());
        
        questionMapper.updateById(question);
        
        // 软删除关联关系
        QuestionTagRel rel = new QuestionTagRel();
        rel.setDeleted(1);
        
        LambdaQueryWrapper<QuestionTagRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionTagRel::getQuestionId, id);
        
        questionTagRelMapper.update(rel, wrapper);
    }
    
    /**
     * 保存试题与标签的关联关系
     */
    private void saveQuestionTagRels(Long questionId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            QuestionTagRel rel = new QuestionTagRel();
            rel.setQuestionId(questionId);
            rel.setTagId(tagId);
            rel.setStatus(1);
            rel.setDeleted(0);
            rel.setCreatedTime(LocalDateTime.now());
            rel.setUpdatedTime(LocalDateTime.now());
            
            questionTagRelMapper.insert(rel);
        }
    }
    
    /**
     * 将实体转换为DTO
     */
    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        BeanUtils.copyProperties(question, dto);
        
        // 查询关联的标签ID
        LambdaQueryWrapper<QuestionTagRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionTagRel::getQuestionId, question.getId());
        wrapper.eq(QuestionTagRel::getDeleted, 0);
        
        List<QuestionTagRel> rels = questionTagRelMapper.selectList(wrapper);
        List<Long> tagIds = rels.stream().map(QuestionTagRel::getTagId).toList();
        
        dto.setTagIds(tagIds);
        
        return dto;
    }
}