package com.gzhennaxia.questionbank.controller;

import com.gzhennaxia.questionbank.dto.KnowledgeTagDTO;
import com.gzhennaxia.questionbank.service.TagService;
import com.gzhennaxia.common.pojo.vo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识点标签控制器
 */
@RestController
@RequestMapping("/api/tag")
@Tag(name = "知识点标签", description = "知识点标签的查询、新增、修改、删除接口")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有标签", description = "获取所有可用的知识点标签")
    public ApiResult<List<KnowledgeTagDTO>> getAllTags() {
        List<KnowledgeTagDTO> tags = tagService.getAllTags();
        return ApiResult.success(tags);
    }

    /**
     * 添加标签
     */
    @PostMapping("/add")
    @Operation(summary = "添加标签", description = "添加新的知识点标签")
    public ApiResult<Map<String, Long>> addTag(@RequestBody KnowledgeTagDTO tagDTO) {
        Long id = tagService.addTag(tagDTO);
        return ApiResult.success(Map.of("id", id));
    }

    /**
     * 修改标签
     */
    @PutMapping("/update")
    @Operation(summary = "修改标签", description = "修改知识点标签信息")
    public ApiResult<Void> updateTag(@RequestBody KnowledgeTagDTO tagDTO) {
        tagService.updateTag(tagDTO);
        return ApiResult.success();
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除标签", description = "删除指定的知识点标签")
    public ApiResult<Void> deleteTag(@RequestParam("id") Long id) {
        tagService.deleteTag(id);
        return ApiResult.success();
    }
}