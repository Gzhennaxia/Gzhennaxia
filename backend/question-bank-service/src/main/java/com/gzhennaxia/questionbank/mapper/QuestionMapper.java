package com.gzhennaxia.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.questionbank.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试题Mapper接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}