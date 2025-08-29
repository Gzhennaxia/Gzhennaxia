package com.personal.timemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.personal.timemanager.entity.Task;
import com.personal.timemanager.mapper.TaskMapper;
import com.personal.timemanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Task createTask(Task task) {
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        taskMapper.insert(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateById(task);
        return task;
    }

    @Override
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskMapper.selectList(null);
    }

    @Override
    public List<Task> getTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return taskMapper.findTasksByDateRange(startDate, endDate);
    }

    @Override
    public List<Task> getTasksByDate(LocalDateTime date) {
        return taskMapper.findTasksByDate(date);
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        return taskMapper.findTasksByStatus(status);
    }

    @Override
    public List<Task> getTasksByCategory(String category) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", category);
        return taskMapper.selectList(queryWrapper);
    }
}