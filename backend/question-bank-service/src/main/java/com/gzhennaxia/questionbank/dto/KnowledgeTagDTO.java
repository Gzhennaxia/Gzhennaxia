package com.gzhennaxia.questionbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 知识点标签DTO
 */
@Data
@Schema(description = "知识点标签数据传输对象")
public class KnowledgeTagDTO {
    
    @Schema(description = "标签ID")
    private Long id;
    
    @Schema(description = "标签名称")
    private String tagName;
}