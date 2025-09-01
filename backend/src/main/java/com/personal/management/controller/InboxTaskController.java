package com.personal.management.controller;

import com.personal.management.dto.InboxTaskCreateRequest;
import com.personal.management.dto.InboxTaskDTO;
import com.personal.management.service.InboxTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inbox-tasks")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InboxTaskController {

    private final InboxTaskService inboxTaskService;

    @GetMapping
    public ResponseEntity<List<InboxTaskDTO>> getAllInboxTasks() {
        List<InboxTaskDTO> inboxTasks = inboxTaskService.getAllInboxTasks();
        return ResponseEntity.ok(inboxTasks);
    }

    @PostMapping
    public ResponseEntity<InboxTaskDTO> createInboxTask(@RequestBody InboxTaskCreateRequest request) {
        InboxTaskDTO inboxTask = inboxTaskService.createInboxTask(request);
        return ResponseEntity.ok(inboxTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InboxTaskDTO> updateInboxTask(@PathVariable Long id, @RequestBody InboxTaskCreateRequest request) {
        InboxTaskDTO inboxTask = inboxTaskService.updateInboxTask(id, request);
        return ResponseEntity.ok(inboxTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInboxTask(@PathVariable Long id) {
        inboxTaskService.deleteInboxTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getInboxTaskCount() {
        long count = inboxTaskService.getInboxTaskCount();
        return ResponseEntity.ok(count);
    }
}