package com.personal.management.controller;

import com.personal.management.dto.TaskCreateRequest;
import com.personal.management.dto.TaskDTO;
import com.personal.management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务相关的API接口")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private final TaskService taskService;
    
    @PostMapping
    @Operation(summary = "创建任务", description = "创建一个新的任务")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskDTO task = taskService.createTask(request);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping("/date/{date}")
    @Operation(summary = "获取指定日期的任务", description = "获取某一天的所有任务")
    public ResponseEntity<List<TaskDTO>> getTasksByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<TaskDTO> tasks = taskService.getTasksByDate(date);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/week/{weekStart}")
    @Operation(summary = "获取周视图任务", description = "获取一周内的所有任务")
    public ResponseEntity<List<TaskDTO>> getWeekTasks(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime weekStart) {
        List<TaskDTO> tasks = taskService.getWeekTasks(weekStart);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/month/{monthStart}")
    @Operation(summary = "获取月视图任务", description = "获取一个月内的所有任务")
    public ResponseEntity<List<TaskDTO>> getMonthTasks(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime monthStart) {
        List<TaskDTO> tasks = taskService.getMonthTasks(monthStart);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/range")
    @Operation(summary = "获取时间范围内的任务", description = "获取指定时间范围内的所有任务")
    public ResponseEntity<List<TaskDTO>> getTasksByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TaskDTO> tasks = taskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }
    
    @PutMapping("/{taskId}/status")
    @Operation(summary = "更新任务状态", description = "更新任务的完成状态")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long taskId, 
            @RequestParam Integer status) {
        TaskDTO task = taskService.updateTaskStatus(taskId, status);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
    
    @GetMapping
    @Operation(summary = "获取所有任务", description = "获取所有任务列表")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情", description = "根据ID获取任务详细信息")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long taskId) {
        TaskDTO task = taskService.convertToDTO(taskService.getById(taskId));
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{taskId}")
    @Operation(summary = "更新任务", description = "更新指定的任务")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskCreateRequest request) {
        TaskDTO task = taskService.updateTask(taskId, request);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{taskId}")
    @Operation(summary = "删除任务", description = "删除指定的任务")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boolean deleted = taskService.removeById(taskId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}