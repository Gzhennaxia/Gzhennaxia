package com.gzhennaxia.personal.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.personal.entity.ib.IBPositionInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 持仓信息 Mapper
 */
@Mapper
public interface IBPositionInfoMapper extends BaseMapper<IBPositionInfo> {
}