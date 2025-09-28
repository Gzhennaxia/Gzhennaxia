package com.gzhennaxia.questionbank.service;

import com.gzhennaxia.questionbank.dto.QuestionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 试题服务接口
 */
public interface QuestionService {

    /**
     * 添加试题
     * @param questionDTO 试题信息
     * @return 新增试题ID
     */
    Long addQuestion(QuestionDTO questionDTO);

    /**
     * 批量导入试题
     * @param file Excel文件
     * @param tagIds 标签ID列表
     * @return 导入结果统计
     */
    Map<String, Integer> importQuestions(MultipartFile file, List<Long> tagIds);

    /**
     * 查询试题列表
     * @param tagId 标签ID
     * @param difficulty 难度
     * @param questionType 题型
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Map<String, Object> listQuestions(Long tagId, String difficulty, String questionType, Integer pageNum, Integer pageSize);

    /**
     * 修改试题
     * @param questionDTO 试题信息
     */
    void updateQuestion(QuestionDTO questionDTO);

    /**
     * 删除试题
     * @param id 试题ID
     */
    void deleteQuestion(Long id);
}