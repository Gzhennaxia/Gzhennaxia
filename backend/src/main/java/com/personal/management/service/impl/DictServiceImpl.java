package com.personal.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.personal.management.base.IBaseServiceImpl;
import com.personal.management.mapper.DictItemMapper;
import com.personal.management.mapper.DictTypeMapper;
import com.personal.management.pojo.converter.DictItemConverter;
import com.personal.management.pojo.converter.DictTypeConverter;
import com.personal.management.pojo.dto.DictItemDto;
import com.personal.management.pojo.dto.DictTypeDto;
import com.personal.management.pojo.entity.DictItem;
import com.personal.management.pojo.entity.DictType;
import com.personal.management.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DictServiceImpl extends IBaseServiceImpl<DictTypeMapper, DictType> implements DictService {

    @Autowired
    private DictItemMapper dictItemMapper;

    /**
     * 获取单个字典（带缓存）
     *
     * @param code 字典编码
     * @return 字典响应数据
     */
    @Override
    @Cacheable(cacheNames = "dict", key = "#code", unless = "#result == null")
    public DictTypeDto getDict(String code) {
        DictType type = baseMapper.selectOne(
                new LambdaQueryWrapper<DictType>()
                        .eq(DictType::getCode, code)
                        .eq(DictType::getStatus, 1)
                        .eq(DictType::getDeleted, false));
        if (type == null) return null;

        List<DictItem> items = dictItemMapper.selectList(
                new LambdaQueryWrapper<DictItem>()
                        .eq(DictItem::getTypeCode, code)
                        .eq(DictItem::getStatus, 1)
                        .orderByAsc(DictItem::getSort, DictItem::getId));

        // convert
        DictTypeDto resp = DictTypeConverter.INSTANCE.toDto(type);
        resp.setDictItems(DictItemConverter.INSTANCE.toDtoList(items));
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
    public Map<String, DictTypeDto> getBatch(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        Map<String, DictTypeDto> result = new HashMap<>();
        for (String code : codes) {
            DictTypeDto dict = getDict(code);
            if (dict != null) {
                result.put(code, dict);
            }
        }
        return result;
    }

    @Override
    public List<DictTypeDto> getAllDicts() {
        List<DictType> dictTypes = baseMapper.selectList(
                new LambdaQueryWrapper<DictType>()
                        .eq(DictType::getDeleted, false)
                        .orderByDesc(DictType::getCreatedTime)
        );
        return DictTypeConverter.INSTANCE.toDtoList(dictTypes);
    }

    @Override
    @Transactional
    public DictTypeDto addDict(DictTypeDto dictTypeDto) {
        // 1. 检查字典编码是否已存在
        DictType existingDict = baseMapper.selectOne(
                new LambdaQueryWrapper<DictType>()
                        .eq(DictType::getCode, dictTypeDto.getCode())
        );
        if (existingDict != null) {
            throw new RuntimeException("字典编码已存在: " + dictTypeDto.getCode());
        }

        // 2. 创建字典类型
        DictType dictType = new DictType();
        dictType.setCode(dictTypeDto.getCode());
        dictType.setName(dictTypeDto.getName());
        dictType.setVersion(dictTypeDto.getVersion() != null ? dictTypeDto.getVersion() : "1");
        dictType.setStatus(dictTypeDto.getStatus() != null ? dictTypeDto.getStatus() : 1);
        dictType.setRemark(dictTypeDto.getRemark());
        dictType.setCreatedTime(LocalDateTime.now().toString());
        dictType.setUpdatedTime(LocalDateTime.now().toString());

        // 3. 保存字典类型
        baseMapper.insert(dictType);

        // 4. 保存字典项
        if (dictTypeDto.getDictItems() != null && !dictTypeDto.getDictItems().isEmpty()) {
            for (int i = 0; i < dictTypeDto.getDictItems().size(); i++) {
                DictItemDto itemDto = dictTypeDto.getDictItems().get(i);
                DictItem dictItem = new DictItem();
                dictItem.setTypeCode(dictTypeDto.getCode());
                dictItem.setItemKey(itemDto.getItemKey());
                dictItem.setItemValue(itemDto.getItemValue());
                dictItem.setStatus(itemDto.getStatus() != null ? itemDto.getStatus() : 1);
                dictItem.setSort(itemDto.getSort() != null ? itemDto.getSort() : i);
                dictItem.setCreatedTime(LocalDateTime.now().toString());
                dictItem.setUpdatedTime(LocalDateTime.now().toString());

                dictItemMapper.insert(dictItem);
            }
        }

        // 5. 返回创建的字典信息
        dictTypeDto.setId(dictType.getId());
        return dictTypeDto;
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

    /**
     * 更新字典状态
     *
     * @param code   字典编码
     * @param status 状态值
     */
    @Override
    @CacheEvict(cacheNames = "dict", key = "#code")
    @Transactional
    public void updateDictStatus(String code, Integer status) {
        DictType type = baseMapper.selectOne(
                new LambdaQueryWrapper<DictType>().eq(DictType::getCode, code));
        if (type == null) {
            throw new RuntimeException("字典不存在: " + code);
        }
        type.setStatus(status);
        type.setUpdatedTime(LocalDateTime.now().toString());
        baseMapper.updateById(type);
    }

    /**
     * 软删除字典
     *
     * @param code 字典编码
     */
    @Override
    @CacheEvict(cacheNames = "dict", key = "#code")
    @Transactional
    public void softDeleteDict(String code) {
        DictType type = baseMapper.selectOne(
                new LambdaQueryWrapper<DictType>().eq(DictType::getCode, code));
        if (type == null) {
            throw new RuntimeException("字典不存在: " + code);
        }
        type.setUpdatedTime(LocalDateTime.now().toString());
        baseMapper.deleteById(type);
    }

    @Override
    @Transactional
    public DictTypeDto updateDict(DictTypeDto dictTypeDto) {
        // 查找现有字典
        DictType existingDict = baseMapper.selectOne(
                new LambdaQueryWrapper<DictType>()
                        .eq(DictType::getCode, dictTypeDto.getCode())
                        .eq(DictType::getDeleted, 0));
        
        if (existingDict == null) {
            throw new RuntimeException("字典不存在: " + dictTypeDto.getCode());
        }

        // 更新字典基本信息
        existingDict.setName(dictTypeDto.getName());
        existingDict.setVersion(dictTypeDto.getVersion());
        existingDict.setStatus(dictTypeDto.getStatus());
        existingDict.setRemark(dictTypeDto.getRemark());
        existingDict.setUpdatedTime(LocalDateTime.now().toString());

        // 保存字典
        baseMapper.updateById(existingDict);

        // 删除现有的字典项
        dictItemMapper.delete(new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getTypeCode, existingDict.getCode()));

        // 添加新的字典项
        if (dictTypeDto.getDictItems() != null && !dictTypeDto.getDictItems().isEmpty()) {
            for (int i = 0; i < dictTypeDto.getDictItems().size(); i++) {
                var itemDto = dictTypeDto.getDictItems().get(i);
                DictItem dictItem = new DictItem();
                dictItem.setTypeCode(existingDict.getCode());
                dictItem.setItemKey(itemDto.getItemKey());
                dictItem.setItemValue(itemDto.getItemValue());
                dictItem.setStatus(itemDto.getStatus());
                dictItem.setSort(i);
                dictItem.setCreatedTime(LocalDateTime.now().toString());
                dictItem.setUpdatedTime(LocalDateTime.now().toString());
                dictItemMapper.insert(dictItem);
            }
        }

        // 更新版本号并清理缓存
        bumpVersionAndEvict(existingDict.getCode(), String.valueOf(existingDict.getVersion()));

        // 转换为DTO返回
        return getDict(existingDict.getCode());
    }

}
