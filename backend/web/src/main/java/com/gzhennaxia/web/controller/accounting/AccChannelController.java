package com.gzhennaxia.web.controller.accounting;

import com.gzhennaxia.accounting.pojo.entity.AccChannel;
import com.gzhennaxia.accounting.service.AccChannelService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收支渠道接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@RestController
@RequestMapping("/api/accounting/channels")
public class AccChannelController {

  private final AccChannelService accChannelService;

  public AccChannelController(AccChannelService accChannelService) {
    this.accChannelService = accChannelService;
  }

  /**
   * 查询全部渠道。
   */
  @GetMapping
  public List<AccChannel> list() {
    return accChannelService.listAll();
  }

  /**
   * 新增渠道；若名称已存在则返回已有 ID。
   */
  @PostMapping
  public Long save(@RequestBody AccChannel channel) {
    return accChannelService.findOrCreateByName(channel.getName());
  }

  @PutMapping
  public boolean update(@RequestBody AccChannel channel) {
    return accChannelService.updateById(channel);
  }

  @DeleteMapping("/{id}")
  public boolean delete(@PathVariable Long id) {
    return accChannelService.removeById(id);
  }
}
