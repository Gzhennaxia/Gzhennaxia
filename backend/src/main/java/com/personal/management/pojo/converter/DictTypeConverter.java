package com.personal.management.pojo.converter;

import com.personal.management.pojo.dto.DictItemDto;
import com.personal.management.pojo.dto.DictTypeDto;
import com.personal.management.pojo.entity.DictItem;
import com.personal.management.pojo.entity.DictType;
import com.personal.management.pojo.vo.DictTypeVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DictTypeConverter {

    DictTypeConverter INSTANCE = Mappers.getMapper(DictTypeConverter.class);


    /**
     * 实体列表转DTO列表
     */
    List<DictTypeDto> toDtoList(List<DictType> dictTypes);

    /**
     * DTO转VO
     */
    DictTypeVo toVo(DictTypeDto dictTypeDto);

    /**
     * DTO列表转VO列表
     */
    List<DictTypeVo> toVoList(List<DictTypeDto> dictTypeDtos);

    /**
     * DictItemDto 转实体
     */
    DictItem itemToEntity(DictItemDto dictItemDto);

    /**
     * 静态方法保持向后兼容
     */
    static List<DictTypeDto> convertToDto(List<DictType> list) {
        return INSTANCE.toDtoList(list);
    }

    static List<DictTypeVo> convert(List<DictTypeDto> dicts) {
        return INSTANCE.toVoList(dicts);
    }

}
