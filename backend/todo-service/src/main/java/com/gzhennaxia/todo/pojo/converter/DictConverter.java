package com.gzhennaxia.todo.pojo.converter;

import com.gzhennaxia.todo.pojo.dto.DictDto;
import com.gzhennaxia.todo.pojo.dto.DictItemDto;
import com.gzhennaxia.todo.pojo.entity.Dict;
import com.gzhennaxia.todo.pojo.entity.DictItem;
import com.gzhennaxia.todo.pojo.vo.DictVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DictConverter {

    // 保留单例实例（非 Spring 环境使用）
    DictConverter INSTANCE = Mappers.getMapper(DictConverter.class);

    /**
     * 实体转 DTO（核心映射方法，集合转换会自动复用）
     */
    DictDto toDto(Dict dict);

    /**
     * 实体列表转 DTO 列表（MapStruct 会自动调用 toDto() 处理每个元素）
     */
    List<DictDto> toDtoList(List<Dict> dictList);

    /**
     * DTO 转 VO
     */
    DictVo toVo(DictDto dictDto);

    /**
     * DTO 列表转 VO 列表（自动复用 toVo() 处理每个元素）
     */
    List<DictVo> toVoList(List<DictDto> dictDtoList);

    /**
     * DictItemDto 转实体
     */
    DictItem itemToEntity(DictItemDto dictItemDto);

}
