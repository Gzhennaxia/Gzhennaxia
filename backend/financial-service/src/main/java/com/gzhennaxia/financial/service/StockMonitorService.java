package com.gzhennaxia.financial.service;

import com.gzhennaxia.common.service.IBaseService;
import com.gzhennaxia.financial.pojo.entity.StockMonitor;
import com.gzhennaxia.financial.pojo.dto.StockMonitorDTO;

public interface StockMonitorService extends IBaseService<StockMonitor> {
    boolean save(StockMonitorDTO stockMonitorDTO);
    
    boolean updateById(StockMonitorDTO stockMonitorDTO);
}