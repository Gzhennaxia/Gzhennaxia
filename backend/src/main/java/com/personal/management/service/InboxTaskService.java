package com.personal.management.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.management.dto.InboxTaskCreateRequest;
import com.personal.management.dto.InboxTaskDTO;
import com.personal.management.entity.InboxTask;
import com.personal.management.mapper.InboxTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InboxTaskService extends ServiceImpl<InboxTaskMapper, InboxTask> {

    public List<InboxTaskDTO> getAllInboxTasks() {
        List<InboxTask> inboxTasks = list();
        return inboxTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InboxTaskDTO createInboxTask(InboxTaskCreateRequest request) {
        InboxTask inboxTask = new InboxTask();
        BeanUtils.copyProperties(request, inboxTask);
        inboxTask.setCreatedAt(LocalDateTime.now());
        inboxTask.setUpdatedAt(LocalDateTime.now());
        
        save(inboxTask);
        return convertToDTO(inboxTask);
    }

    public InboxTaskDTO updateInboxTask(Long id, InboxTaskCreateRequest request) {
        InboxTask inboxTask = getById(id);
        if (inboxTask == null) {
            throw new RuntimeException("收集箱任务不存在");
        }
        
        BeanUtils.copyProperties(request, inboxTask);
        inboxTask.setUpdatedAt(LocalDateTime.now());
        
        updateById(inboxTask);
        return convertToDTO(inboxTask);
    }

    public void deleteInboxTask(Long id) {
        removeById(id);
    }

    public long getInboxTaskCount() {
        return count();
    }

    private InboxTaskDTO convertToDTO(InboxTask inboxTask) {
        InboxTaskDTO dto = new InboxTaskDTO();
        BeanUtils.copyProperties(inboxTask, dto);
        return dto;
    }
}