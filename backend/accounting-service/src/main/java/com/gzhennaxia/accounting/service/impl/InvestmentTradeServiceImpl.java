package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.accounting.mapper.InvestmentTradeMapper;
import com.gzhennaxia.accounting.pojo.entity.InvestmentTrade;
import com.gzhennaxia.accounting.pojo.request.InvestmentTradeRequest;
import com.gzhennaxia.accounting.service.InvestmentTradeService;
import com.gzhennaxia.common.enums.ResponseCode;
import com.gzhennaxia.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvestmentTradeServiceImpl extends ServiceImpl<InvestmentTradeMapper, InvestmentTrade>
    implements InvestmentTradeService {

  @Override
  public List<InvestmentTrade> listAll() {
    return list(new LambdaQueryWrapper<InvestmentTrade>().orderByDesc(InvestmentTrade::getTradeTime));
  }

  @Override
  public Long saveTrade(InvestmentTradeRequest request) {
    if (request.getSymbolId() == null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "标的不能为空");
    }
    if (!StringUtils.hasText(request.getTradeType())) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "交易类型不能为空");
    }
    if (request.getAmount() == null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "成交金额不能为空");
    }
    InvestmentTrade trade = new InvestmentTrade();
    BeanUtils.copyProperties(request, trade);
    if (trade.getTradeTime() == null) {
      trade.setTradeTime(LocalDateTime.now());
    }
    if (trade.getFee() == null) {
      trade.setFee(BigDecimal.ZERO);
    }
    save(trade);
    return trade.getId();
  }

  @Override
  public boolean removeTrade(Long id) {
    return removeById(id);
  }
}
