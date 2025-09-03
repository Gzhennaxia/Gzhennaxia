package com.personal.management.pojo.converter;

import com.personal.management.pojo.dto.DictItemDto;
import com.personal.management.pojo.dto.DictTypeDto;
import com.personal.management.pojo.entity.DictItem;
import com.personal.management.pojo.entity.DictType;
import com.personal.management.pojo.vo.DictTypeVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DictTypeConverter {

    // 保留单例实例（非 Spring 环境使用）
    DictTypeConverter INSTANCE = Mappers.getMapper(DictTypeConverter.class);

    /**
     * 实体转 DTO（核心映射方法，集合转换会自动复用）
     */
    DictTypeDto toDto(DictType dictType);

    /**
     * 实体列表转 DTO 列表（MapStruct 会自动调用 toDto() 处理每个元素）
     */
    List<DictTypeDto> toDtoList(List<DictType> dictTypes);

    /**
     * DTO 转 VO
     */
    DictTypeVo toVo(DictTypeDto dictTypeDto);

    /**
     * DTO 列表转 VO 列表（自动复用 toVo() 处理每个元素）
     */
    List<DictTypeVo> toVoList(List<DictTypeDto> dictTypeDtos);

    /**
     * DictItemDto 转实体
     */
    DictItem itemToEntity(DictItemDto dictItemDto);

}
