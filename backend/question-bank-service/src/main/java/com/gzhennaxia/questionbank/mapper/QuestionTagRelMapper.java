package com.gzhennaxia.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.questionbank.entity.QuestionTagRel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试题-标签关联Mapper接口
 */
@Mapper
public interface QuestionTagRelMapper extends BaseMapper<QuestionTagRel> {
}