package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.accounting.mapper.AccCategoryMapper;
import com.gzhennaxia.accounting.pojo.entity.AccCategory;
import com.gzhennaxia.accounting.service.AccCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AccCategoryServiceImpl extends ServiceImpl<AccCategoryMapper, AccCategory>
    implements AccCategoryService {

  @Override
  public List<AccCategory> listByType(String type) {
    LambdaQueryWrapper<AccCategory> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(type)) {
      wrapper.eq(AccCategory::getType, type);
    }
    wrapper.orderByAsc(AccCategory::getId);
    return list(wrapper);
  }
}
