package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.accounting.mapper.FundAccountMapper;
import com.gzhennaxia.accounting.pojo.entity.FundAccount;
import com.gzhennaxia.accounting.pojo.request.FundAccountRequest;
import com.gzhennaxia.accounting.service.FundAccountService;
import com.gzhennaxia.common.enums.ResponseCode;
import com.gzhennaxia.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FundAccountServiceImpl extends ServiceImpl<FundAccountMapper, FundAccount>
    implements FundAccountService {

  @Override
  public List<FundAccount> listAll() {
    return list(new LambdaQueryWrapper<FundAccount>()
        .orderByAsc(FundAccount::getSortOrder)
        .orderByAsc(FundAccount::getId));
  }

  @Override
  public Long saveAccount(FundAccountRequest request) {
    validateRequest(request, false);
    FundAccount account = new FundAccount();
    BeanUtils.copyProperties(request, account);
    if (account.getBalance() == null) {
      account.setBalance(BigDecimal.ZERO);
    }
    if (!StringUtils.hasText(account.getCurrency())) {
      account.setCurrency("CNY");
    }
    if (account.getIncludeInNetWorth() == null) {
      account.setIncludeInNetWorth(1);
    }
    if (account.getSortOrder() == null) {
      account.setSortOrder(0);
    }
    save(account);
    return account.getId();
  }

  @Override
  public boolean updateAccount(FundAccountRequest request) {
    validateRequest(request, true);
    FundAccount account = getById(request.getId());
    if (account == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND, "账户不存在");
    }
    BeanUtils.copyProperties(request, account);
    return updateById(account);
  }

  @Override
  public boolean removeAccount(Long id) {
    return removeById(id);
  }

  private void validateRequest(FundAccountRequest request, boolean requireId) {
    if (requireId && request.getId() == null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "账户ID不能为空");
    }
    if (!StringUtils.hasText(request.getName())) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "账户名称不能为空");
    }
    if (!StringUtils.hasText(request.getType())) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "账户类型不能为空");
    }
  }
}
