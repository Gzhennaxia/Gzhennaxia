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
    public IBContract getByConid(String conid) {
        return contractMapper.selectByConid(conid);
    }

    @Override
    public List<IBContract> getAllContracts() {
        return contractMapper.selectList(null);
    }

}
