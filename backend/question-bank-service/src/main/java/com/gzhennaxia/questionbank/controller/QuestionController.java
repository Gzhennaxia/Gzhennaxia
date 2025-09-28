package com.gzhennaxia.questionbank.controller;

import com.gzhennaxia.questionbank.dto.QuestionDTO;
import com.gzhennaxia.questionbank.service.QuestionService;
import com.gzhennaxia.common.pojo.vo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 试题管理控制器
 */
@RestController
@RequestMapping("/api/question")
@Tag(name = "试题管理", description = "试题的查询、新增、修改、删除接口")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 添加试题
     */
    @PostMapping("/add")
    @Operation(summary = "添加试题", description = "手动添加单个试题")
    public ApiResult<Map<String, Long>> addQuestion(@RequestBody QuestionDTO questionDTO) {
        Long id = questionService.addQuestion(questionDTO);
        return ApiResult.success(Map.of("id", id));
    }

    /**
     * 批量导入试题
     */
    @PostMapping("/import")
    @Operation(summary = "批量导入试题", description = "通过Excel批量导入试题")
    public ApiResult<Map<String, Integer>> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds) {
        Map<String, Integer> result = questionService.importQuestions(file, tagIds);
        return ApiResult.success(result);
    }

    /**
     * 查询试题列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询试题列表", description = "按条件查询试题列表")
    public ApiResult<Map<String, Object>> listQuestions(
            @RequestParam(value = "tagId", required = false) Long tagId,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "questionType", required = false) String questionType,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = questionService.listQuestions(tagId, difficulty, questionType, pageNum, pageSize);
        return ApiResult.success(result);
    }

    /**
     * 修改试题
     */
    @PutMapping("/update")
    @Operation(summary = "修改试题", description = "修改试题信息")
    public ApiResult<Void> updateQuestion(@RequestBody QuestionDTO questionDTO) {
        questionService.updateQuestion(questionDTO);
        return ApiResult.success();
    }

    /**
     * 删除试题
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除试题", description = "删除指定试题")
    public ApiResult<Void> deleteQuestion(@RequestParam("id") Long id) {
        questionService.deleteQuestion(id);
        return ApiResult.success();
    }
}