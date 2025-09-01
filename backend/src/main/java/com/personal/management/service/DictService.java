package com.personal.management.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.personal.management.entity.DictItem;
import com.personal.management.entity.DictType;
import com.personal.management.mapper.DictItemMapper;
import com.personal.management.mapper.DictTypeMapper;
import com.personal.management.vo.DictResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DictService {
    private final DictTypeMapper typeMapper;
    private final DictItemMapper itemMapper;

    public DictService(DictTypeMapper typeMapper, DictItemMapper itemMapper) {
        this.typeMapper = typeMapper;
        this.itemMapper = itemMapper;
    }

    /**
     * 获取单个字典（带缓存）
     */
    @Cacheable(cacheNames = "dict", key = "#code", unless = "#result == null")
    public DictResponse getDict(String code) {
        DictType type = typeMapper.selectOne(
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
     */
    public Map<String, String> getVersions(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        List<DictType> types = typeMapper.selectList(
                new LambdaQueryWrapper<DictType>()
                        .in(DictType::getCode, codes)
                        .eq(DictType::getStatus, 1));
        Map<String, String> map = new HashMap<>();
        for (DictType t : types) map.put(t.getCode(), t.getVersion());
        return map;
    }

    /**
     * 字典发生变更后：更新版本号并清缓存
     */
    @CacheEvict(cacheNames = "dict", key = "#code")
    @Transactional
    public void bumpVersionAndEvict(String code, String newVersion) {
        DictType type = typeMapper.selectOne(
                new LambdaQueryWrapper<DictType>().eq(DictType::getCode, code));
        if (type == null) return;
        type.setVersion(newVersion);
        type.setUpdateTime(LocalDateTime.now().toString());
        typeMapper.updateById(type);
    }

    /**
     * 帮助方法：生成版本号
     */
    public static String nextVersion() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(LocalDateTime.now());
    }
}