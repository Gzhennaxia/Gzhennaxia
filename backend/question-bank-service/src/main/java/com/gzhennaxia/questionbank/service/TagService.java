package com.gzhennaxia.questionbank.service;

import com.gzhennaxia.questionbank.dto.KnowledgeTagDTO;
import java.util.List;

/**
 * 知识点标签服务接口
 */
public interface TagService {
    
    /**
     * 获取所有标签
     */
    List<KnowledgeTagDTO> getAllTags();
    
    /**
     * 添加标签
     */
    Long addTag(KnowledgeTagDTO tagDTO);
    
    /**
     * 更新标签
     */
    void updateTag(KnowledgeTagDTO tagDTO);
    
    /**
     * 删除标签
     */
    void deleteTag(Long id);
}