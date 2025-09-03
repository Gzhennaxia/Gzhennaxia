package com.personal.management.service;

import com.personal.management.base.IBaseService;
import com.personal.management.pojo.dto.DictTypeDto;
import com.personal.management.pojo.entity.DictType;
import com.personal.management.pojo.request.DictTypeCreateRequest;
import com.personal.management.vo.DictResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author Gzhennaxia
 */
public interface DictService extends IBaseService<DictType> {

    void bumpVersionAndEvict(String code, String version);

    DictTypeDto getDict(String code);

    Map<String, String> getVersions(List<String> codes);

    Map<String, DictTypeDto> getBatch(List<String> codes);

    List<DictTypeDto> getAllDicts();

    DictTypeDto addDict(DictTypeDto dictTypeDto);
}