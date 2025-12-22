package com.gzhennaxia.web.controller.finance;

import com.gzhennaxia.common.controller.IBaseController;
import com.gzhennaxia.common.service.IBaseService;
import com.gzhennaxia.financial.pojo.entity.StockMonitor;
import com.gzhennaxia.financial.service.StockMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockMonitorController extends IBaseController<StockMonitor> {

    @Autowired
    private StockMonitorService stockMonitorService;

    @Override
    protected IBaseService<StockMonitor> getBaseService() {
        return stockMonitorService;
    }
}
