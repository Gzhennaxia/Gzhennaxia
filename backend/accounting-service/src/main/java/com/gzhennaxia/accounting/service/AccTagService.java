package com.gzhennaxia.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.accounting.pojo.entity.AccTag;
import com.gzhennaxia.accounting.pojo.vo.AccTagVO;

import java.util.List;
import java.util.Map;

/**
 * 标签服务。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
public interface AccTagService extends IService<AccTag> {

  /**
   * 查询全部标签，按流水引用次数降序、名称升序。
   */
  List<AccTagVO> listAll();

  /**
   * 新建标签（名称不可重复）。
   */
  Long createTag(AccTag tag);

  /**
   * 更新标签名称与颜色。
   */
  boolean updateTag(AccTag tag);

  /**
   * 按名称查找，不存在则创建并返回 ID。
   *
   * @param name 标签名
   * @return 标签主键
   */
  Long findOrCreateByName(String name);

  /**
   * 批量按名称解析为 ID（不存在则创建）。
   */
  List<Long> resolveIdsByNames(List<String> names);

  /**
   * 根据 ID 列表加载 id→name 映射。
   */
  Map<Long, String> loadNameMapByIds(List<Long> tagIds);
}
