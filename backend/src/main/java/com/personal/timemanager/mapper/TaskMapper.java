package com.personal.timemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.timemanager.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    @Select("SELECT * FROM tasks WHERE start_time >= #{startDate} AND start_time < #{endDate} AND deleted = 0 ORDER BY start_time")
    List<Task> findTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Select("SELECT * FROM tasks WHERE DATE(start_time) = DATE(#{date}) AND deleted = 0 ORDER BY start_time")
    List<Task> findTasksByDate(LocalDateTime date);
    
    @Select("SELECT * FROM tasks WHERE status = #{status} AND deleted = 0 ORDER BY start_time")
    List<Task> findTasksByStatus(String status);
}