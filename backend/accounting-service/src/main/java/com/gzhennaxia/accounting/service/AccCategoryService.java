package com.gzhennaxia.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.accounting.pojo.entity.AccCategory;

import java.util.List;

public interface AccCategoryService extends IService<AccCategory> {

  List<AccCategory> listByType(String type);
}
