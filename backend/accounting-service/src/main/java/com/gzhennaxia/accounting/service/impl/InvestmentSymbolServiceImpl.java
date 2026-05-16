package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.accounting.mapper.InvestmentSymbolMapper;
import com.gzhennaxia.accounting.pojo.entity.InvestmentSymbol;
import com.gzhennaxia.accounting.service.InvestmentSymbolService;
import org.springframework.stereotype.Service;

@Service
public class InvestmentSymbolServiceImpl extends ServiceImpl<InvestmentSymbolMapper, InvestmentSymbol>
    implements InvestmentSymbolService {
}
