package com.personal.management.service;

import com.personal.management.base.IBaseService;
import com.personal.management.entity.DictType;
import com.personal.management.vo.DictResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Gzhennaxia
 */
public interface DictService extends IBaseService<DictType> {

    void bumpVersionAndEvict(String code, String version);

    DictResponse getDict(String code);

    Map<String, String> getVersions(List<String> codes);

    Map<String, DictResponse> getBatch(List<String> codes);
}