package com.gzhennaxia.personal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.personal.entity.ib.IBContract;
import com.gzhennaxia.personal.mapper.IBContractMapper;
import com.gzhennaxia.personal.service.IBContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * IB合约信息服务实现
 */
@Service
@RequiredArgsConstructor
public class IBContractServiceImpl extends ServiceImpl<IBContractMapper, IBContract> implements IBContractService {

    private final IBContractMapper contractMapper;

    @Override
    public boolean saveContract(IBContract contract) {
        if (contract.getConid() == null || contract.getConid().isEmpty()) {
            throw new IllegalArgumentException("合约ID不能为空");
        }
        return contractMapper.insert(contract) > 0;
    }

    @Override
    public IBContract getByConid(String conid) {
        return contractMapper.selectByConid(conid);
    }

    @Override
    public List<IBContract> getAllContracts() {
        return contractMapper.selectList(null);
    }

}
