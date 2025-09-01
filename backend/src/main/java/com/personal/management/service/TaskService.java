package com.personal.management.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.management.dto.TaskCreateRequest;
import com.personal.management.dto.TaskDTO;
import com.personal.management.entity.Task;
import com.personal.management.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService extends ServiceImpl<TaskMapper, Task> {
    
    private final ObjectMapper objectMapper;
    
    public TaskDTO createTask(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStartTime(request.getStartTime());
        task.setEndTime(request.getEndTime());
        task.setPriority(request.getPriority());
        task.setStatus(0); // 默认待办状态
        task.setCategory(request.getCategory());
        task.setReminderTime(request.getReminderTime());
        task.setIsAllDay(request.getIsAllDay());
        task.setRepeatType(request.getRepeatType());
        task.setRepeatEndDate(request.getRepeatEndDate());
        
        // 处理标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            try {
                task.setTags(objectMapper.writeValueAsString(request.getTags()));
            } catch (JsonProcessingException e) {
                task.setTags("[]");
            }
        } else {
            task.setTags("[]");
        }
        
        save(task);
        return convertToDTO(task);
    }
    
    public List<TaskDTO> getTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Task> tasks = baseMapper.findTasksByDateRange(startDate, endDate);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TaskDTO> getTasksByDate(LocalDateTime date) {
        List<Task> tasks = baseMapper.findTasksByDate(date);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TaskDTO> getWeekTasks(LocalDateTime weekStart) {
        LocalDateTime weekEnd = weekStart.plusDays(7);
        return getTasksByDateRange(weekStart, weekEnd);
    }
    
    public List<TaskDTO> getMonthTasks(LocalDateTime monthStart) {
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        return getTasksByDateRange(monthStart, monthEnd);
    }
    
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = list();
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public TaskDTO updateTaskStatus(Long taskId, Integer status) {
        Task task = getById(taskId);
        if (task != null) {
            task.setStatus(status);
            updateById(task);
            return convertToDTO(task);
        }
        return null;
    }

    public TaskDTO updateTask(Long taskId, TaskCreateRequest request) {
        Task existingTask = getById(taskId);
        if (existingTask != null) {
            existingTask.setTitle(request.getTitle());
            existingTask.setDescription(request.getDescription());
            existingTask.setStartTime(request.getStartTime());
            existingTask.setEndTime(request.getEndTime());
            existingTask.setPriority(request.getPriority());
            existingTask.setCategory(request.getCategory());
            existingTask.setIsAllDay(request.getIsAllDay());
            existingTask.setRepeatType(request.getRepeatType());
            existingTask.setRepeatEndDate(request.getRepeatEndDate());
            existingTask.setReminderTime(request.getReminderTime());
            existingTask.setStatus(request.getStatus());

            // 处理标签
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                existingTask.setTags(String.join(",", request.getTags()));
            }

            updateById(existingTask);
            return convertToDTO(existingTask);
        }
        return null;
    }
    
    public TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStartTime(task.getStartTime());
        dto.setEndTime(task.getEndTime());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setCategory(task.getCategory());
        dto.setReminderTime(task.getReminderTime());
        dto.setIsAllDay(task.getIsAllDay());
        dto.setRepeatType(task.getRepeatType());
        dto.setRepeatEndDate(task.getRepeatEndDate());
        dto.setCreatedTime(task.getCreatedTime());
        dto.setUpdatedTime(task.getUpdatedTime());
        
        // 解析标签
        try {
            if (task.getTags() != null && !task.getTags().isEmpty()) {
                List<String> tags = objectMapper.readValue(task.getTags(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                dto.setTags(tags);
            }
        } catch (JsonProcessingException e) {
            dto.setTags(new ArrayList<>());
        }
        
        return dto;
    }
    
    public Map<String, Integer> getTaskStats() {
        List<Task> allTasks = list();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        Map<String, Integer> stats = new HashMap<>();
        
        int overdueCount = 0;
        int todayCount = 0;
        int completedCount = 0;
        
        for (Task task : allTasks) {
            if (task.getStatus() == 1) { // 已完成
                completedCount++;
            } else if (task.getStatus() == 0) { // 待办
                LocalDateTime endTime = task.getEndTime();
                if (endTime != null) {
                    if (endTime.isBefore(todayStart)) {
                        overdueCount++; // 已过期
                    } else if (endTime.isAfter(todayStart) && endTime.isBefore(todayEnd)) {
                        todayCount++; // 今天
                    }
                }
            }
        }
        
        stats.put("overdue", overdueCount);
        stats.put("today", todayCount);
        stats.put("completed", completedCount);
        stats.put("total", allTasks.size());
        
        return stats;
    }
}