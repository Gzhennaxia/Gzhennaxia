package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.accounting.mapper.AccChannelMapper;
import com.gzhennaxia.accounting.pojo.entity.AccChannel;
import com.gzhennaxia.accounting.service.AccChannelService;
import com.gzhennaxia.common.enums.ResponseCode;
import com.gzhennaxia.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道服务实现。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Service
public class AccChannelServiceImpl extends ServiceImpl<AccChannelMapper, AccChannel> implements AccChannelService {

  private static final int MAX_NAME_LEN = 50;

  @Override
  public List<AccChannel> listAll() {
    return list(new LambdaQueryWrapper<AccChannel>()
        .orderByAsc(AccChannel::getSortOrder)
        .orderByAsc(AccChannel::getName));
  }

  @Override
  public Long findOrCreateByName(String name) {
    String normalized = normalizeName(name);
    AccChannel existing = getOne(new LambdaQueryWrapper<AccChannel>().eq(AccChannel::getName, normalized));
    if (existing != null) {
      return existing.getId();
    }
    AccChannel channel = new AccChannel();
    channel.setName(normalized);
    channel.setSortOrder(0);
    save(channel);
    return channel.getId();
  }

  @Override
  public Map<Long, String> loadNameMapByIds(List<Long> channelIds) {
    Map<Long, String> map = new HashMap<>();
    if (channelIds == null || channelIds.isEmpty()) {
      return map;
    }
    List<AccChannel> channels = listByIds(channelIds);
    for (AccChannel channel : channels) {
      map.put(channel.getId(), channel.getName());
    }
    return map;
  }

  private String normalizeName(String name) {
    if (!StringUtils.hasText(name)) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "渠道名称不能为空");
    }
    String trimmed = name.trim();
    if (trimmed.length() > MAX_NAME_LEN) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "渠道名称不能超过 " + MAX_NAME_LEN + " 个字符");
    }
    return trimmed;
  }
}
