package com.gzhennaxia.personal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.personal.entity.ib.IBContract;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IB合约信息Mapper接口
 */
@Mapper
public interface IBContractMapper extends BaseMapper<IBContract> {

    /**
     * 根据合约ID查询合约信息
     * @param conid 合约ID
     * @return 合约信息
     */
    IBContract selectByConid(String conid);

    /**
     * 批量插入合约信息
     * @param contracts 合约列表
     * @return 插入记录数
     */
    int batchInsert(List<IBContract> contracts);
}
