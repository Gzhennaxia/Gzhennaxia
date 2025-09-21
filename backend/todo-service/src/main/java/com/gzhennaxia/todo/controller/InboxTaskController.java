package com.gzhennaxia.todo.controller;

import com.gzhennaxia.todo.dto.InboxTaskCreateRequest;
import com.gzhennaxia.todo.dto.InboxTaskDTO;
import com.gzhennaxia.todo.service.InboxTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inboxTask")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InboxTaskController {

    private final InboxTaskService inboxTaskService;

    @GetMapping
    public List<InboxTaskDTO> getAllInboxTasks() {
        return inboxTaskService.getAllInboxTasks();
    }

    @PostMapping
    public InboxTaskDTO createInboxTask(@RequestBody InboxTaskCreateRequest request) {
        return inboxTaskService.createInboxTask(request);
    }

    @PutMapping("/{id}")
    public InboxTaskDTO updateInboxTask(@PathVariable Long id, @RequestBody InboxTaskCreateRequest request) {
        return inboxTaskService.updateInboxTask(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteInboxTask(@PathVariable Long id) {
        inboxTaskService.deleteInboxTask(id);
    }

    @GetMapping("/count")
    public Long getInboxTaskCount() {
        return inboxTaskService.getInboxTaskCount();
    }
}