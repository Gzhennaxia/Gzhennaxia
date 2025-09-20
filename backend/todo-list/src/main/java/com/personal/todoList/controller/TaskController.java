package com.personal.management.controller;

import com.personal.management.dto.TaskCreateRequest;
import com.personal.management.dto.TaskDTO;
import com.personal.management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务相关的API接口")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "创建任务", description = "创建一个新的任务")
    public TaskDTO createTask(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.createTask(request);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "获取指定日期的任务", description = "获取某一天的所有任务")
    public List<TaskDTO> getTasksByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return taskService.getTasksByDate(date);
    }

    @GetMapping("/week/{weekStart}")
    @Operation(summary = "获取周视图任务", description = "获取一周内的所有任务")
    public List<TaskDTO> getWeekTasks(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime weekStart) {
        return taskService.getWeekTasks(weekStart);
    }

    @GetMapping("/month/{monthStart}")
    @Operation(summary = "获取月视图任务", description = "获取一个月内的所有任务")
    public List<TaskDTO> getMonthTasks(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime monthStart) {
        return taskService.getMonthTasks(monthStart);
    }

    @GetMapping("/range")
    @Operation(summary = "获取时间范围内的任务", description = "获取指定时间范围内的所有任务")
    public List<TaskDTO> getTasksByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return taskService.getTasksByDateRange(startDate, endDate);
    }

    @PutMapping("/{taskId}/status")
    @Operation(summary = "更新任务状态", description = "更新任务的完成状态")
    public TaskDTO updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam Integer status) {
        return taskService.updateTaskStatus(taskId, status);
    }

    @GetMapping
    @Operation(summary = "获取所有任务", description = "获取所有任务列表")
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取任务统计", description = "获取任务数量统计信息")
    public Map<String, Integer> getTaskStats() {
        return taskService.getTaskStats();
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情", description = "根据ID获取任务详细信息")
    public TaskDTO getTask(@PathVariable Long taskId) {
        return taskService.convertToDTO(taskService.getById(taskId));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "更新任务", description = "更新指定的任务")
    public TaskDTO updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskCreateRequest request) {
        return taskService.updateTask(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "删除任务", description = "删除指定的任务")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.removeById(taskId);
    }
}