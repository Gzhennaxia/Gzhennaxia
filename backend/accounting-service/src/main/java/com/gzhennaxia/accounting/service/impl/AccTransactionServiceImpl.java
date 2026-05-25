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
import com.gzhennaxia.accounting.imports.AccTransactionFileParser;
import com.gzhennaxia.accounting.imports.AccTransactionImportRow;
import com.gzhennaxia.accounting.imports.AccTransactionImportSupport;
import com.gzhennaxia.accounting.pojo.request.AccTransactionRequest;
import com.gzhennaxia.accounting.pojo.vo.AccTransactionImportErrorVO;
import com.gzhennaxia.accounting.pojo.vo.AccTransactionImportResultVO;
import com.gzhennaxia.accounting.pojo.vo.AccTransactionVO;
import com.gzhennaxia.accounting.service.AccChannelService;
import com.gzhennaxia.accounting.service.AccTagService;
import com.gzhennaxia.accounting.service.AccTransactionService;
import com.gzhennaxia.accounting.util.AccTagIdsUtil;
import com.gzhennaxia.common.enums.ResponseCode;
import com.gzhennaxia.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccTransactionServiceImpl extends ServiceImpl<AccTransactionMapper, AccTransaction>
    implements AccTransactionService {

  private final FundAccountMapper fundAccountMapper;
  private final AccCategoryMapper accCategoryMapper;
  private final AccTransactionFileParser accTransactionFileParser;
  private final AccTagService accTagService;
  private final AccChannelService accChannelService;

  public AccTransactionServiceImpl(FundAccountMapper fundAccountMapper,
      AccCategoryMapper accCategoryMapper,
      AccTransactionFileParser accTransactionFileParser,
      AccTagService accTagService,
      AccChannelService accChannelService) {
    this.fundAccountMapper = fundAccountMapper;
    this.accCategoryMapper = accCategoryMapper;
    this.accTransactionFileParser = accTransactionFileParser;
    this.accTagService = accTagService;
    this.accChannelService = accChannelService;
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
  public AccTransactionImportResultVO importFromFile(MultipartFile file) {
    List<AccTransactionImportRow> rows = accTransactionFileParser.parse(file);
    AccTransactionImportResultVO result = new AccTransactionImportResultVO();
    result.setTotalRows(rows.size());
    if (rows.isEmpty()) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "文件中没有可导入的数据行");
    }
    Map<String, Long> accountIdByName = loadAccountIdByName();
    Map<String, Long> categoryIdByKey = loadCategoryIdByKey();
    for (AccTransactionImportRow row : rows) {
      try {
        AccTransactionRequest request = AccTransactionImportSupport.toRequest(row, accountIdByName,
            categoryIdByKey);
        applyTagIdsFromImport(request, row.getTags());
        saveTransaction(request);
        result.setSuccessCount(result.getSuccessCount() + 1);
      } catch (BusinessException ex) {
        result.setFailCount(result.getFailCount() + 1);
        AccTransactionImportErrorVO error = new AccTransactionImportErrorVO();
        error.setRowNumber(row.getRowNumber());
        error.setMessage(ex.getCustomMessage());
        result.getErrors().add(error);
      } catch (Exception ex) {
        result.setFailCount(result.getFailCount() + 1);
        AccTransactionImportErrorVO error = new AccTransactionImportErrorVO();
        error.setRowNumber(row.getRowNumber());
        error.setMessage(ex.getMessage() != null ? ex.getMessage() : "导入失败");
        result.getErrors().add(error);
      }
    }
    return result;
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
    tx.setTags(AccTagIdsUtil.encode(request.getTagIds()));
    if (id != null) {
      tx.setId(id);
    }
    if (tx.getTradeTime() == null) {
      tx.setTradeTime(LocalDateTime.now());
    }
    return tx;
  }

  private void applyTagIdsFromImport(AccTransactionRequest request, String tagsText) {
    if (!StringUtils.hasText(tagsText)) {
      return;
    }
    List<Long> ids = new ArrayList<>();
    for (String part : tagsText.split(",")) {
      String trimmed = part.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      try {
        ids.add(Long.parseLong(trimmed));
      } catch (NumberFormatException ex) {
        ids.add(accTagService.findOrCreateByName(trimmed));
      }
    }
    request.setTagIds(ids.stream().distinct().toList());
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
    List<Long> tagIds = AccTagIdsUtil.decode(tx.getTags());
    vo.setTagIds(tagIds);
    Map<Long, String> tagNameMap = accTagService.loadNameMapByIds(tagIds);
    vo.setTagNames(tagIds.stream()
        .map(tagNameMap::get)
        .filter(name -> name != null)
        .collect(Collectors.toList()));
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
    if (tx.getChannelId() != null) {
      Map<Long, String> channelNames = accChannelService.loadNameMapByIds(List.of(tx.getChannelId()));
      vo.setChannelName(channelNames.get(tx.getChannelId()));
    }
    return vo;
  }

  private Map<String, Long> loadAccountIdByName() {
    List<FundAccount> accounts = fundAccountMapper.selectList(null);
    Map<String, Long> map = new HashMap<>();
    for (FundAccount account : accounts) {
      if (StringUtils.hasText(account.getName())) {
        map.put(account.getName().trim(), account.getId());
      }
    }
    return map;
  }

  private Map<String, Long> loadCategoryIdByKey() {
    List<AccCategory> categories = accCategoryMapper.selectList(null);
    Map<String, Long> map = new HashMap<>();
    for (AccCategory category : categories) {
      if (StringUtils.hasText(category.getName()) && StringUtils.hasText(category.getType())) {
        map.put(AccTransactionImportSupport.categoryKey(category.getType(), category.getName()),
            category.getId());
      }
    }
    return map;
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
