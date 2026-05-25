package com.gzhennaxia.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.accounting.pojo.entity.AccChannel;

import java.util.List;
import java.util.Map;

/**
 * 渠道服务。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
public interface AccChannelService extends IService<AccChannel> {

  /**
   * 查询全部渠道，按排序与名称。
   */
  List<AccChannel> listAll();

  /**
   * 按名称查找，不存在则创建并返回 ID。
   *
   * @param name 渠道名
   * @return 渠道主键
   */
  Long findOrCreateByName(String name);

  /**
   * 根据 ID 列表加载 id→name 映射。
   */
  Map<Long, String> loadNameMapByIds(List<Long> channelIds);
}
