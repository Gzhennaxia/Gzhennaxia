package com.gzhennaxia.financial.service.impl;

import com.gzhennaxia.common.service.impl.IBaseServiceImpl;
import com.gzhennaxia.financial.mapper.StockMonitorMapper;
import com.gzhennaxia.financial.pojo.entity.StockMonitor;
import com.gzhennaxia.financial.service.StockMonitorService;
import org.springframework.stereotype.Service;

@Service
public class StockMonitorServiceImpl extends IBaseServiceImpl<StockMonitorMapper, StockMonitor> implements StockMonitorService {
}
