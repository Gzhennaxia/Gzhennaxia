package com.gzhennaxia.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.common.pojo.entity.DictItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DictItemMapper extends BaseMapper<DictItem> {

    void physicalDelete(@Param("ew") LambdaQueryWrapper<DictItem> wrapper);

}