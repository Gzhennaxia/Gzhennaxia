package com.personal.management.service;

import com.personal.management.base.IBaseService;
import com.personal.management.pojo.dto.DictDto;
import com.personal.management.pojo.entity.Dict;

import java.util.List;
import java.util.Map;

/**
 * @author Gzhennaxia
 */
public interface DictService extends IBaseService<Dict> {

    void bumpVersionAndEvict(String code, String version);

    DictDto getDict(String code);

    Map<String, String> getVersions(List<String> codes);

    Map<String, DictDto> getBatch(List<String> codes);

    List<DictDto> getAllDicts();

    DictDto addDict(DictDto dictDto);

    void updateDictStatus(String code, Integer status);

    void softDeleteDict(String code);

    DictDto updateDict(DictDto dictDto);
}