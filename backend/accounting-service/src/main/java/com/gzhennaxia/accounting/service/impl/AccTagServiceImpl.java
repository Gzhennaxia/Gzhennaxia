package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.accounting.constant.AccTagColorConstants;
import com.gzhennaxia.accounting.mapper.AccTagMapper;
import com.gzhennaxia.accounting.pojo.entity.AccTag;
import com.gzhennaxia.accounting.pojo.vo.AccTagVO;
import com.gzhennaxia.accounting.service.AccTagService;
import com.gzhennaxia.common.enums.ResponseCode;
import com.gzhennaxia.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签服务实现。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Service
public class AccTagServiceImpl extends ServiceImpl<AccTagMapper, AccTag> implements AccTagService {

  private static final int MAX_NAME_LEN = 50;

  @Override
  public List<AccTagVO> listAll() {
    List<AccTag> tags = list(new LambdaQueryWrapper<AccTag>().orderByAsc(AccTag::getName));
    List<AccTagVO> result = new ArrayList<>();
    for (AccTag tag : tags) {
      AccTagVO vo = toVo(tag);
      vo.setTransactionCount(baseMapper.countTransactionUsage(String.valueOf(tag.getId())));
      result.add(vo);
    }
    result.sort(Comparator
        .comparing(AccTagVO::getTransactionCount, Comparator.reverseOrder())
        .thenComparing(AccTagVO::getName));
    return result;
  }

  @Override
  public Long createTag(AccTag tag) {
    String normalized = normalizeName(tag.getName());
    AccTag existing = getOne(new LambdaQueryWrapper<AccTag>().eq(AccTag::getName, normalized));
    if (existing != null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "标签名称已存在: " + normalized);
    }
    AccTag entity = new AccTag();
    entity.setName(normalized);
    entity.setColor(AccTagColorConstants.resolveColor(tag.getColor(), null));
    entity.setSortOrder(tag.getSortOrder() != null ? tag.getSortOrder() : 0);
    save(entity);
    return entity.getId();
  }

  @Override
  public boolean updateTag(AccTag tag) {
    if (tag.getId() == null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "标签ID不能为空");
    }
    AccTag existing = getById(tag.getId());
    if (existing == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND, "标签不存在");
    }
    String normalized = normalizeName(tag.getName());
    AccTag duplicate = getOne(new LambdaQueryWrapper<AccTag>()
        .eq(AccTag::getName, normalized)
        .ne(AccTag::getId, tag.getId()));
    if (duplicate != null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "标签名称已存在: " + normalized);
    }
    existing.setName(normalized);
    existing.setColor(AccTagColorConstants.resolveColor(tag.getColor(), tag.getId()));
    if (tag.getSortOrder() != null) {
      existing.setSortOrder(tag.getSortOrder());
    }
    return updateById(existing);
  }

  @Override
  public Long findOrCreateByName(String name) {
    String normalized = normalizeName(name);
    AccTag existing = getOne(new LambdaQueryWrapper<AccTag>().eq(AccTag::getName, normalized));
    if (existing != null) {
      return existing.getId();
    }
    AccTag tag = new AccTag();
    tag.setName(normalized);
    tag.setSortOrder(0);
    save(tag);
    tag.setColor(AccTagColorConstants.resolveColor(null, tag.getId()));
    updateById(tag);
    return tag.getId();
  }

  @Override
  public List<Long> resolveIdsByNames(List<String> names) {
    if (names == null || names.isEmpty()) {
      return List.of();
    }
    List<Long> ids = new ArrayList<>();
    for (String name : names) {
      if (StringUtils.hasText(name)) {
        ids.add(findOrCreateByName(name));
      }
    }
    return ids.stream().distinct().toList();
  }

  @Override
  public Map<Long, String> loadNameMapByIds(List<Long> tagIds) {
    Map<Long, String> map = new HashMap<>();
    if (tagIds == null || tagIds.isEmpty()) {
      return map;
    }
    List<AccTag> tags = listByIds(tagIds);
    for (AccTag tag : tags) {
      map.put(tag.getId(), tag.getName());
    }
    return map;
  }

  private AccTagVO toVo(AccTag tag) {
    AccTagVO vo = new AccTagVO();
    BeanUtils.copyProperties(tag, vo);
    return vo;
  }

  private String normalizeName(String name) {
    if (!StringUtils.hasText(name)) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "标签名称不能为空");
    }
    String trimmed = name.trim();
    if (trimmed.length() > MAX_NAME_LEN) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "标签名称不能超过 " + MAX_NAME_LEN + " 个字符");
    }
    return trimmed;
  }
}
