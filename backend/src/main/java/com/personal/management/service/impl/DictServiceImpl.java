package com.personal.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.personal.management.base.IBaseServiceImpl;
import com.personal.management.mapper.DictItemMapper;
import com.personal.management.mapper.DictTypeMapper;
import com.personal.management.pojo.converter.DictTypeConverter;
import com.personal.management.pojo.dto.DictTypeDto;
import com.personal.management.pojo.entity.DictItem;
import com.personal.management.pojo.entity.DictType;
import com.personal.management.service.DictService;
import com.personal.management.vo.DictResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DictServiceImpl extends IBaseServiceImpl<DictTypeMapper, DictType> implements DictService {

    @Autowired
    private DictItemMapper itemMapper;

    /**
     * 获取单个字典（带缓存）
     *
     * @param code 字典编码
     * @return 字典响应数据
     */
    @Override
    @Cacheable(cacheNames = "dict", key = "#code", unless = "#result == null")
    public DictResponse getDict(String code) {
        DictType type = baseMapper.selectOne(
                new LambdaQueryWrapper<DictType>()
                        .eq(DictType::getCode, code)
                        .eq(DictType::getStatus, 1));
        if (type == null) return null;

        List<DictItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<DictItem>()
                        .eq(DictItem::getTypeCode, code)
                        .eq(DictItem::getStatus, 1)
                        .orderByAsc(DictItem::getSort, DictItem::getId));

        DictResponse resp = new DictResponse();
        resp.setDictCode(code);
        resp.setVersion(type.getVersion());
        List<DictResponse.Item> list = new ArrayList<>();
        for (DictItem it : items) {
            DictResponse.Item vo = new DictResponse.Item();
            vo.key = it.getItemKey();
            vo.value = it.getItemValue();
            vo.sort = it.getSort();
            list.add(vo);
        }
        resp.setItems(list);
        return resp;
    }

    /**
     * 批量获取版本号
     *
     * @param codes 字典编码集合
     * @return 字典编码与版本号的映射
     */
    @Override
    public Map<String, String> getVersions(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        List<DictType> types = baseMapper.selectList(
                new LambdaQueryWrapper<DictType>()
                        .in(DictType::getCode, codes)
                        .eq(DictType::getStatus, 1));
        Map<String, String> map = new HashMap<>();
        for (DictType t : types) map.put(t.getCode(), t.getVersion());
        return map;
    }

    /**
     * 批量获取多个字典数据
     *
     * @param codes 字典编码集合
     * @return 字典编码与字典数据的映射
     */
    @Override
    public Map<String, DictResponse> getBatch(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        Map<String, DictResponse> result = new HashMap<>();
        for (String code : codes) {
            DictResponse dict = getDict(code);
            if (dict != null) {
                result.put(code, dict);
            }
        }
        return result;
    }

    @Override
    public List<DictTypeDto> getAllDicts() {
        return DictTypeConverter.convertToDto(this.list());
    }

    /**
     * 字典发生变更后：更新版本号并清缓存
     *
     * @param code       字典编码
     * @param newVersion 新版本号
     */
    @Override
    @CacheEvict(cacheNames = "dict", key = "#code")
    @Transactional
    public void bumpVersionAndEvict(String code, String newVersion) {
        DictType type = baseMapper.selectOne(
                new LambdaQueryWrapper<DictType>().eq(DictType::getCode, code));
        if (type == null) return;
        type.setVersion(newVersion);
        type.setUpdatedTime(LocalDateTime.now().toString());
        baseMapper.updateById(type);
    }


}
