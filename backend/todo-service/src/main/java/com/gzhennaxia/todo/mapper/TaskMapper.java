package com.gzhennaxia.todo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.todo.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    @Select("SELECT * FROM tasks WHERE deleted = 0 AND " +
            "((start_time >= #{startDate} AND start_time < #{endDate}) OR " +
            "(end_time >= #{startDate} AND end_time < #{endDate}) OR " +
            "(start_time < #{startDate} AND end_time >= #{endDate})) " +
            "ORDER BY start_time ASC")
    List<Task> findTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Select("SELECT * FROM tasks WHERE deleted = 0 AND " +
            "DATE(start_time) = DATE(#{date}) " +
            "ORDER BY start_time ASC")
    List<Task> findTasksByDate(LocalDateTime date);
}