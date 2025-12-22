package com.gzhennaxia.financial.service.impl;

import com.gzhennaxia.common.service.impl.IBaseServiceImpl;
import com.gzhennaxia.financial.mapper.StockMonitorMapper;
import com.gzhennaxia.financial.pojo.entity.StockMonitor;
import com.gzhennaxia.financial.pojo.dto.StockMonitorDTO;
import com.gzhennaxia.financial.service.StockMonitorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class StockMonitorServiceImpl extends IBaseServiceImpl<StockMonitorMapper, StockMonitor> implements StockMonitorService {
    
    @Override
    public boolean save(StockMonitorDTO stockMonitorDTO) {
        StockMonitor stockMonitor = new StockMonitor();
        BeanUtils.copyProperties(stockMonitorDTO, stockMonitor);
        return this.save(stockMonitor);
    }
    
    @Override
    public boolean updateById(StockMonitorDTO stockMonitorDTO) {
        StockMonitor stockMonitor = new StockMonitor();
        BeanUtils.copyProperties(stockMonitorDTO, stockMonitor);
        return this.updateById(stockMonitor);
    }
}