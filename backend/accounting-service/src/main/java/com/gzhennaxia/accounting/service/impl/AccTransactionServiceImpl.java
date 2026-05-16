package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.accounting.constant.AccountingConstants;
import com.gzhennaxia.accounting.mapper.AccCategoryMapper;
import com.gzhennaxia.accounting.mapper.AccTransactionMapper;
import com.gzhennaxia.accounting.mapper.FundAccountMapper;
import com.gzhennaxia.accounting.pojo.entity.AccCategory;
import com.gzhennaxia.accounting.pojo.entity.AccTransaction;
import com.gzhennaxia.accounting.pojo.entity.FundAccount;
import com.gzhennaxia.accounting.pojo.request.AccTransactionRequest;
import com.gzhennaxia.accounting.pojo.vo.AccTransactionVO;
import com.gzhennaxia.accounting.service.AccTransactionService;
import com.gzhennaxia.common.enums.ResponseCode;
import com.gzhennaxia.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccTransactionServiceImpl extends ServiceImpl<AccTransactionMapper, AccTransaction>
    implements AccTransactionService {

  private final FundAccountMapper fundAccountMapper;
  private final AccCategoryMapper accCategoryMapper;

  public AccTransactionServiceImpl(FundAccountMapper fundAccountMapper, AccCategoryMapper accCategoryMapper) {
    this.fundAccountMapper = fundAccountMapper;
    this.accCategoryMapper = accCategoryMapper;
  }

  @Override
  public IPage<AccTransactionVO> pageTransactions(int pageNo, int pageSize, String type, Long accountId,
      Long categoryId, LocalDateTime startTime, LocalDateTime endTime, String keyword) {
    LambdaQueryWrapper<AccTransaction> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(type)) {
      wrapper.eq(AccTransaction::getType, type);
    }
    if (accountId != null) {
      wrapper.and(w -> w.eq(AccTransaction::getAccountId, accountId)
          .or().eq(AccTransaction::getTargetAccountId, accountId));
    }
    if (categoryId != null) {
      wrapper.eq(AccTransaction::getCategoryId, categoryId);
    }
    if (startTime != null) {
      wrapper.ge(AccTransaction::getTradeTime, startTime);
    }
    if (endTime != null) {
      wrapper.le(AccTransaction::getTradeTime, endTime);
    }
    if (StringUtils.hasText(keyword)) {
      wrapper.and(w -> w.like(AccTransaction::getNote, keyword)
          .or().like(AccTransaction::getPayee, keyword)
          .or().like(AccTransaction::getTags, keyword));
    }
    wrapper.orderByDesc(AccTransaction::getTradeTime);
    IPage<AccTransaction> page = page(new Page<>(pageNo, pageSize), wrapper);
    return page.convert(this::toVo);
  }

  @Override
  public List<AccTransactionVO> listRecent(int limit) {
    List<AccTransaction> list = list(new LambdaQueryWrapper<AccTransaction>()
        .orderByDesc(AccTransaction::getTradeTime)
        .last("LIMIT " + limit));
    return list.stream().map(this::toVo).collect(Collectors.toList());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long saveTransaction(AccTransactionRequest request) {
    AccTransaction tx = buildEntity(request, null);
    applyBalanceChange(tx, false);
    save(tx);
    return tx.getId();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateTransaction(AccTransactionRequest request) {
    if (request.getId() == null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "流水ID不能为空");
    }
    AccTransaction old = getById(request.getId());
    if (old == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND, "流水不存在");
    }
    applyBalanceChange(old, true);
    AccTransaction tx = buildEntity(request, old.getId());
    applyBalanceChange(tx, false);
    return updateById(tx);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeTransaction(Long id) {
    AccTransaction old = getById(id);
    if (old == null) {
      return false;
    }
    applyBalanceChange(old, true);
    return removeById(id);
  }

  private AccTransaction buildEntity(AccTransactionRequest request, Long id) {
    validate(request);
    AccTransaction tx = new AccTransaction();
    BeanUtils.copyProperties(request, tx);
    if (id != null) {
      tx.setId(id);
    }
    if (tx.getTradeTime() == null) {
      tx.setTradeTime(LocalDateTime.now());
    }
    return tx;
  }

  private void validate(AccTransactionRequest request) {
    if (!StringUtils.hasText(request.getType())) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "流水类型不能为空");
    }
    if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "金额必须大于0");
    }
    if (request.getAccountId() == null) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "账户不能为空");
    }
    if (AccountingConstants.TX_TRANSFER.equals(request.getType())) {
      if (request.getTargetAccountId() == null) {
        throw new BusinessException(ResponseCode.BAD_REQUEST, "转账必须选择目标账户");
      }
      if (request.getAccountId().equals(request.getTargetAccountId())) {
        throw new BusinessException(ResponseCode.BAD_REQUEST, "转账账户不能相同");
      }
    }
  }

  private void applyBalanceChange(AccTransaction tx, boolean reverse) {
    BigDecimal amount = tx.getAmount();
    int dir = reverse ? -1 : 1;
    FundAccount from = requireAccount(tx.getAccountId());
    switch (tx.getType()) {
      case AccountingConstants.TX_EXPENSE ->
          adjustBalance(from, amount.multiply(BigDecimal.valueOf(-dir)));
      case AccountingConstants.TX_INCOME ->
          adjustBalance(from, amount.multiply(BigDecimal.valueOf(dir)));
      case AccountingConstants.TX_TRANSFER -> {
        FundAccount to = requireAccount(tx.getTargetAccountId());
        adjustBalance(from, amount.multiply(BigDecimal.valueOf(-dir)));
        adjustBalance(to, amount.multiply(BigDecimal.valueOf(dir)));
      }
      default -> throw new BusinessException(ResponseCode.BAD_REQUEST, "不支持的流水类型: " + tx.getType());
    }
  }

  private FundAccount requireAccount(Long accountId) {
    FundAccount account = fundAccountMapper.selectById(accountId);
    if (account == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND, "账户不存在: " + accountId);
    }
    return account;
  }

  private void adjustBalance(FundAccount account, BigDecimal delta) {
    BigDecimal balance = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
    account.setBalance(balance.add(delta));
    fundAccountMapper.updateById(account);
  }

  private AccTransactionVO toVo(AccTransaction tx) {
    AccTransactionVO vo = new AccTransactionVO();
    BeanUtils.copyProperties(tx, vo);
    Map<Long, String> accountNames = loadAccountNames();
    vo.setAccountName(accountNames.get(tx.getAccountId()));
    if (tx.getTargetAccountId() != null) {
      vo.setTargetAccountName(accountNames.get(tx.getTargetAccountId()));
    }
    if (tx.getCategoryId() != null) {
      AccCategory category = accCategoryMapper.selectById(tx.getCategoryId());
      if (category != null) {
        vo.setCategoryName(category.getName());
      }
    }
    return vo;
  }

  private Map<Long, String> loadAccountNames() {
    List<FundAccount> accounts = fundAccountMapper.selectList(null);
    Map<Long, String> map = new HashMap<>();
    for (FundAccount a : accounts) {
      map.put(a.getId(), a.getName());
    }
    return map;
  }
}
