package com.personal.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.personal.management.base.IBaseServiceImpl;
import com.personal.management.mapper.DictItemMapper;
import com.personal.management.mapper.DictMapper;
import com.personal.management.pojo.converter.DictConverter;
import com.personal.management.pojo.converter.DictItemConverter;
import com.personal.management.pojo.dto.DictDto;
import com.personal.management.pojo.dto.DictItemDto;
import com.personal.management.pojo.entity.Dict;
import com.personal.management.pojo.entity.DictItem;
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
public class DictServiceImpl extends IBaseServiceImpl<DictMapper, Dict> implements DictService {

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
    public DictDto getDict(String code) {
        Dict type = baseMapper.selectOne(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, code));
        if (type == null) return null;

        List<DictItem> items = dictItemMapper.selectList(
                new LambdaQueryWrapper<DictItem>()
                        .eq(DictItem::getDictCode, code)
                        .eq(DictItem::getStatus, 1)
                        .orderByAsc(DictItem::getSort, DictItem::getId));

        // convert
        DictDto resp = DictConverter.INSTANCE.toDto(type);
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
        List<Dict> types = baseMapper.selectList(
                new LambdaQueryWrapper<Dict>()
                        .in(Dict::getDictCode, codes)
                        .eq(Dict::getStatus, 1));
        Map<String, String> map = new HashMap<>();
        for (Dict t : types) map.put(t.getDictCode(), t.getVersion());
        return map;
    }

    /**
     * 批量获取多个字典数据
     *
     * @param codes 字典编码集合
     * @return 字典编码与字典数据的映射
     */
    @Override
    public Map<String, DictDto> getBatch(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        Map<String, DictDto> result = new HashMap<>();
        for (String code : codes) {
            DictDto dict = getDict(code);
            if (dict != null) {
                result.put(code, dict);
            }
        }
        return result;
    }

    @Override
    public List<DictDto> getAllDicts() {
        List<Dict> dicts = baseMapper.selectList(
                new LambdaQueryWrapper<Dict>()
                        .eq(Dict::getDeleted, false)
                        .orderByDesc(Dict::getCreatedTime)
        );
        return DictConverter.INSTANCE.toDtoList(dicts);
    }

    @Override
    @Transactional
    public DictDto addDict(DictDto dictDto) {
        // 1. 检查字典编码是否已存在
        Dict existingDict = baseMapper.selectOne(
                new LambdaQueryWrapper<Dict>()
                        .eq(Dict::getDictCode, dictDto.getDictCode())
        );
        if (existingDict != null) {
            throw new RuntimeException("字典编码已存在: " + dictDto.getDictCode());
        }

        // 2. 创建字典类型
        Dict dict = new Dict();
        dict.setDictCode(dictDto.getDictCode());
        dict.setDictName(dictDto.getDictName());
        dict.setVersion(dictDto.getVersion() != null ? dictDto.getVersion() : "1");
        dict.setStatus(dictDto.getStatus() != null ? dictDto.getStatus() : 1);
        dict.setRemark(dictDto.getRemark());
        dict.setCreatedTime(LocalDateTime.now().toString());
        dict.setUpdatedTime(LocalDateTime.now().toString());

        // 3. 保存字典类型
        baseMapper.insert(dict);

        // 4. 保存字典项
        if (dictDto.getDictItems() != null && !dictDto.getDictItems().isEmpty()) {
            for (int i = 0; i < dictDto.getDictItems().size(); i++) {
                DictItemDto itemDto = dictDto.getDictItems().get(i);
                DictItem dictItem = new DictItem();
                dictItem.setDictCode(dictDto.getDictCode());
                dictItem.setItemCode(itemDto.getItemKey());
                dictItem.setItemName(itemDto.getItemValue());
                dictItem.setStatus(itemDto.getStatus() != null ? itemDto.getStatus() : 1);
                dictItem.setSort(itemDto.getSort() != null ? itemDto.getSort() : i);
                dictItem.setCreatedTime(LocalDateTime.now().toString());
                dictItem.setUpdatedTime(LocalDateTime.now().toString());

                dictItemMapper.insert(dictItem);
            }
        }

        // 5. 返回创建的字典信息
        dictDto.setId(dict.getId());
        return dictDto;
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
        Dict type = baseMapper.selectOne(
                new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, code));
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
        Dict type = baseMapper.selectOne(
                new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, code));
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
        Dict type = baseMapper.selectOne(
                new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, code));
        if (type == null) {
            throw new RuntimeException("字典不存在: " + code);
        }
        type.setUpdatedTime(LocalDateTime.now().toString());
        baseMapper.deleteById(type);
    }

    @Override
    @Transactional
    public DictDto updateDict(DictDto dictDto) {
        // 查找现有字典
        Dict existingDict = baseMapper.selectOne(
                new LambdaQueryWrapper<Dict>()
                        .eq(Dict::getDictCode, dictDto.getDictCode())
                        .eq(Dict::getDeleted, 0));

        if (existingDict == null) {
            throw new RuntimeException("字典不存在: " + dictDto.getDictCode());
        }

        // 更新字典基本信息
        existingDict.setDictName(dictDto.getDictName());
        existingDict.setVersion(dictDto.getVersion());
        existingDict.setStatus(dictDto.getStatus());
        existingDict.setRemark(dictDto.getRemark());
        existingDict.setUpdatedTime(LocalDateTime.now().toString());

        // 保存字典
        baseMapper.updateById(existingDict);

        // 删除现有的字典项
        dictItemMapper.delete(new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getDictCode, existingDict.getDictCode()));

        // 添加新的字典项
        if (dictDto.getDictItems() != null && !dictDto.getDictItems().isEmpty()) {
            for (int i = 0; i < dictDto.getDictItems().size(); i++) {
                var itemDto = dictDto.getDictItems().get(i);
                DictItem dictItem = new DictItem();
                dictItem.setDictCode(existingDict.getDictCode());
                dictItem.setItemCode(itemDto.getItemKey());
                dictItem.setItemName(itemDto.getItemValue());
                dictItem.setStatus(itemDto.getStatus());
                dictItem.setSort(i);
                dictItem.setCreatedTime(LocalDateTime.now().toString());
                dictItem.setUpdatedTime(LocalDateTime.now().toString());
                dictItemMapper.insert(dictItem);
            }
        }

        // 更新版本号并清理缓存
        bumpVersionAndEvict(existingDict.getDictCode(), String.valueOf(existingDict.getVersion()));

        // 转换为DTO返回
        return getDict(existingDict.getDictCode());
    }

}
