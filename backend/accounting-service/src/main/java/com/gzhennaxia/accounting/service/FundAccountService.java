package com.gzhennaxia.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.accounting.pojo.entity.FundAccount;
import com.gzhennaxia.accounting.pojo.request.FundAccountRequest;

import java.util.List;

public interface FundAccountService extends IService<FundAccount> {

  List<FundAccount> listAll();

  Long saveAccount(FundAccountRequest request);

  boolean updateAccount(FundAccountRequest request);

  boolean removeAccount(Long id);
}
