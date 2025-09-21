package com.gzhennaxia.todo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.todo.entity.InboxTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InboxTaskMapper extends BaseMapper<InboxTask> {
}