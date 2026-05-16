package com.gzhennaxia.accounting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.accounting.pojo.entity.AccTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccTransactionMapper extends BaseMapper<AccTransaction> {
}
