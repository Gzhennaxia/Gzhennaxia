package com.gzhennaxia.web.controller.accounting;

import com.gzhennaxia.accounting.pojo.entity.AccTag;
import com.gzhennaxia.accounting.pojo.vo.AccTagVO;
import com.gzhennaxia.accounting.service.AccTagService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 流水标签接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@RestController
@RequestMapping("/api/accounting/tags")
public class AccTagController {

  private final AccTagService accTagService;

  public AccTagController(AccTagService accTagService) {
    this.accTagService = accTagService;
  }

  @GetMapping
  public List<AccTagVO> list() {
    return accTagService.listAll();
  }

  /**
   * 管理页新建标签（名称不可重复，可指定颜色）。
   */
  @PostMapping
  public Long save(@RequestBody AccTag tag) {
    return accTagService.createTag(tag);
  }

  /**
   * 记一笔等场景：按名称查找，不存在则创建。
   */
  @PostMapping("/quick")
  public Long quickSave(@RequestBody AccTag tag) {
    return accTagService.findOrCreateByName(tag.getName());
  }

  @PutMapping
  public boolean update(@RequestBody AccTag tag) {
    return accTagService.updateTag(tag);
  }

  @DeleteMapping("/{id}")
  public boolean delete(@PathVariable Long id) {
    return accTagService.removeById(id);
  }

  /**
   * 按名称批量解析/创建标签，返回 ID 列表。
   */
  @PostMapping("/resolve")
  public List<Long> resolve(@RequestBody Map<String, List<String>> body) {
    List<String> names = body.get("names");
    return accTagService.resolveIdsByNames(names);
  }
}
