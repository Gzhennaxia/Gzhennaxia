package com.personal.management.service;

import com.personal.management.pojo.dto.DictItemDto;
import com.personal.management.pojo.dto.DictTypeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@SpringBootTest
@Transactional
public class DictServiceTest {

    @Autowired
    private DictService dictService;

    @Test
    public void testAddDict() {
        // 准备测试数据
        DictTypeDto dictTypeDto = new DictTypeDto();
        dictTypeDto.setCode("TEST");
        dictTypeDto.setName("测试");
        dictTypeDto.setVersion("1");
        dictTypeDto.setStatus(1);
        dictTypeDto.setRemark("测试");

        // 准备字典项
        DictItemDto item1 = new DictItemDto();
        item1.setItemKey("10");
        item1.setItemValue("test1");
        item1.setStatus(1);
        item1.setSort(0);

        DictItemDto item2 = new DictItemDto();
        item2.setItemKey("20");
        item2.setItemValue("test2");
        item2.setStatus(1);
        item2.setSort(1);

        dictTypeDto.setDictItems(Arrays.asList(item1, item2));

        // 执行测试
        DictTypeDto result = dictService.addDict(dictTypeDto);

        // 验证结果
        assert result != null;
        assert result.getId() != null;
        assert "TEST".equals(result.getCode());
        assert "测试".equals(result.getName());
    }
}