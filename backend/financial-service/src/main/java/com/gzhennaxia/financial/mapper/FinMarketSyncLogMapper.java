package com.gzhennaxia.financial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.financial.pojo.entity.FinMarketSyncLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行情同步日志 Mapper。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Mapper
public interface FinMarketSyncLogMapper extends BaseMapper<FinMarketSyncLog> {
}
