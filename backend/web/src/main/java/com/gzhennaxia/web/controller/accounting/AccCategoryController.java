package com.gzhennaxia.web.controller.accounting;

import com.gzhennaxia.accounting.pojo.entity.AccCategory;
import com.gzhennaxia.accounting.service.AccCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收支分类接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@RestController
@RequestMapping("/api/accounting/categories")
public class AccCategoryController {

  private final AccCategoryService accCategoryService;

  public AccCategoryController(AccCategoryService accCategoryService) {
    this.accCategoryService = accCategoryService;
  }

  @GetMapping
  public List<AccCategory> list(@RequestParam(required = false) String type) {
    return accCategoryService.listByType(type);
  }

  @PostMapping
  public boolean save(@RequestBody AccCategory category) {
    return accCategoryService.save(category);
  }

  @PutMapping
  public boolean update(@RequestBody AccCategory category) {
    return accCategoryService.updateById(category);
  }
}
