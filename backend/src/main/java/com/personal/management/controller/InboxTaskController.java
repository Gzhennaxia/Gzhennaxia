package com.personal.management.controller;

import com.personal.management.dto.InboxTaskCreateRequest;
import com.personal.management.dto.InboxTaskDTO;
import com.personal.management.service.InboxTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inbox-tasks")
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