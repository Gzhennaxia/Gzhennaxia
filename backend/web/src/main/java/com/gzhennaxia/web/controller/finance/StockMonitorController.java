package com.gzhennaxia.web.controller.finance;

import com.gzhennaxia.common.controller.IBaseController;
import com.gzhennaxia.common.service.IBaseService;
import com.gzhennaxia.financial.pojo.entity.StockMonitor;
import com.gzhennaxia.financial.pojo.request.StockMonitorRequest;
import com.gzhennaxia.financial.pojo.dto.StockMonitorDTO;
import com.gzhennaxia.financial.service.StockMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/stock-monitor")
public class StockMonitorController extends IBaseController<StockMonitor> {

    @Autowired
    private StockMonitorService stockMonitorService;

    @Override
    protected IBaseService<StockMonitor> getBaseService() {
        return stockMonitorService;
    }
    
    /**
     * 新增股票监控
     */
    @PostMapping
    public boolean save(@RequestBody StockMonitorRequest request) {
        StockMonitorDTO stockMonitorDTO = new StockMonitorDTO();
        // 这里应该使用类似 BeanUtils.copyProperties 的方法进行转换
        // 为了简化，我们手动设置属性
        stockMonitorDTO.setExchange(request.getExchange());
        stockMonitorDTO.setStockCode(request.getStockCode());
        stockMonitorDTO.setStockName(request.getStockName());
        stockMonitorDTO.setCurrencySymbol(request.getCurrencySymbol());
        stockMonitorDTO.setPrice1yAgo(request.getPrice1yAgo());
        stockMonitorDTO.setRise1y(request.getRise1y());
        stockMonitorDTO.setPrice6mAgo(request.getPrice6mAgo());
        stockMonitorDTO.setRise6m(request.getRise6m());
        stockMonitorDTO.setPrice3mAgo(request.getPrice3mAgo());
        stockMonitorDTO.setRise3m(request.getRise3m());
        stockMonitorDTO.setPrice1mAgo(request.getPrice1mAgo());
        stockMonitorDTO.setRise1m(request.getRise1m());
        stockMonitorDTO.setPrice1wAgo(request.getPrice1wAgo());
        stockMonitorDTO.setRise1w(request.getRise1w());
        stockMonitorDTO.setPrice3dAgo(request.getPrice3dAgo());
        stockMonitorDTO.setRise3d(request.getRise3d());
        stockMonitorDTO.setPriceYesterday(request.getPriceYesterday());
        stockMonitorDTO.setRiseYesterday(request.getRiseYesterday());
        stockMonitorDTO.setCacheCreateTime(request.getCacheCreateTime());
        stockMonitorDTO.setCacheExpireTime(request.getCacheExpireTime());
        stockMonitorDTO.setStatus(request.getStatus());
        stockMonitorDTO.setRemark(request.getRemark());
        
        return stockMonitorService.save(stockMonitorDTO);
    }
    
    /**
     * 更新股票监控
     */
    @PutMapping
    public boolean updateById(@RequestBody StockMonitorRequest request) {
        StockMonitorDTO stockMonitorDTO = new StockMonitorDTO();
        // 这里应该使用类似 BeanUtils.copyProperties 的方法进行转换
        // 为了简化，我们手动设置属性
        stockMonitorDTO.setId(request.getId());
        stockMonitorDTO.setExchange(request.getExchange());
        stockMonitorDTO.setStockCode(request.getStockCode());
        stockMonitorDTO.setStockName(request.getStockName());
        stockMonitorDTO.setCurrencySymbol(request.getCurrencySymbol());
        stockMonitorDTO.setPrice1yAgo(request.getPrice1yAgo());
        stockMonitorDTO.setRise1y(request.getRise1y());
        stockMonitorDTO.setPrice6mAgo(request.getPrice6mAgo());
        stockMonitorDTO.setRise6m(request.getRise6m());
        stockMonitorDTO.setPrice3mAgo(request.getPrice3mAgo());
        stockMonitorDTO.setRise3m(request.getRise3m());
        stockMonitorDTO.setPrice1mAgo(request.getPrice1mAgo());
        stockMonitorDTO.setRise1m(request.getRise1m());
        stockMonitorDTO.setPrice1wAgo(request.getPrice1wAgo());
        stockMonitorDTO.setRise1w(request.getRise1w());
        stockMonitorDTO.setPrice3dAgo(request.getPrice3dAgo());
        stockMonitorDTO.setRise3d(request.getRise3d());
        stockMonitorDTO.setPriceYesterday(request.getPriceYesterday());
        stockMonitorDTO.setRiseYesterday(request.getRiseYesterday());
        stockMonitorDTO.setCacheCreateTime(request.getCacheCreateTime());
        stockMonitorDTO.setCacheExpireTime(request.getCacheExpireTime());
        stockMonitorDTO.setStatus(request.getStatus());
        stockMonitorDTO.setRemark(request.getRemark());
        
        return stockMonitorService.updateById(stockMonitorDTO);
    }
}