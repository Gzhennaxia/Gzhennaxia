package com.gzhennaxia.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gzhennaxia.questionbank.dto.KnowledgeTagDTO;
import com.gzhennaxia.questionbank.entity.KnowledgeTag;
import com.gzhennaxia.questionbank.mapper.KnowledgeTagMapper;
import com.gzhennaxia.questionbank.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识点标签服务实现类
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private KnowledgeTagMapper knowledgeTagMapper;

    @Override
    public List<KnowledgeTagDTO> getAllTags() {
        LambdaQueryWrapper<KnowledgeTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeTag::getDeleted, 0)
               .eq(KnowledgeTag::getStatus, 1)
               .orderByAsc(KnowledgeTag::getTagName);
        
        return knowledgeTagMapper.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long addTag(KnowledgeTagDTO tagDTO) {
        KnowledgeTag tag = new KnowledgeTag();
        BeanUtils.copyProperties(tagDTO, tag);
        
        // 设置默认值
        tag.setStatus(1);
        tag.setDeleted(0);
        tag.setCreatedTime(LocalDateTime.now());
        tag.setUpdatedTime(LocalDateTime.now());
        
        knowledgeTagMapper.insert(tag);
        return tag.getId();
    }

    @Override
    public void updateTag(KnowledgeTagDTO tagDTO) {
        KnowledgeTag tag = knowledgeTagMapper.selectById(tagDTO.getId());
        if (tag != null) {
            tag.setTagName(tagDTO.getTagName());
            tag.setUpdatedTime(LocalDateTime.now());
            knowledgeTagMapper.updateById(tag);
        }
    }

    @Override
    public void deleteTag(Long id) {
        KnowledgeTag tag = knowledgeTagMapper.selectById(id);
        if (tag != null) {
            // 软删除
            tag.setDeleted(1);
            tag.setUpdatedTime(LocalDateTime.now());
            knowledgeTagMapper.updateById(tag);
        }
    }
    
    /**
     * 将实体转换为DTO
     */
    private KnowledgeTagDTO convertToDTO(KnowledgeTag tag) {
        KnowledgeTagDTO dto = new KnowledgeTagDTO();
        BeanUtils.copyProperties(tag, dto);
        return dto;
    }
}