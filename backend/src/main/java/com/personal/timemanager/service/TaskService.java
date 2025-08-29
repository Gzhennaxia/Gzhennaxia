package com.personal.timemanager.service;

import com.personal.timemanager.entity.Task;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task updateTask(Task task);
    void deleteTask(Long id);
    Task getTaskById(Long id);
    List<Task> getAllTasks();
    List<Task> getTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Task> getTasksByDate(LocalDateTime date);
    List<Task> getTasksByStatus(String status);
    List<Task> getTasksByCategory(String category);
}