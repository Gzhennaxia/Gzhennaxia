package com.gzhennaxia.todo.pojo.converter;


import com.gzhennaxia.todo.pojo.dto.DictItemDto;
import com.gzhennaxia.todo.pojo.entity.DictItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DictItemConverter {

    DictItemConverter INSTANCE = Mappers.getMapper(DictItemConverter.class);

    DictItemDto toDto(DictItem dictItem);

    List<DictItemDto> toDtoList(List<DictItem> dictItems);

}
