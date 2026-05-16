package com.gzhennaxia.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.accounting.pojo.entity.InvestmentTrade;
import com.gzhennaxia.accounting.pojo.request.InvestmentTradeRequest;

import java.util.List;

public interface InvestmentTradeService extends IService<InvestmentTrade> {

  List<InvestmentTrade> listAll();

  Long saveTrade(InvestmentTradeRequest request);

  boolean removeTrade(Long id);
}
