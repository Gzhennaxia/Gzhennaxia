package com.gzhennaxia.personal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.personal.entity.ib.IBMarketHistoryData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 历史市场数据 Mapper
 */
@Mapper
public interface IBMarketHistoryDataMapper extends BaseMapper<IBMarketHistoryData> {

    /**
     * 根据合约ID和时间范围查询历史数据
     *
     * @param conid     合约ID
     * @param timeRange 时间范围
     * @return K线数据列表
     */
    List<IBMarketHistoryData> selectByConidAndTimeRange(@Param("conid") String conid, 
                                                        @Param("timeRange") String timeRange);

    /**
     * 根据合约ID、时间范围和时间段查询历史数据
     *
     * @param conid     合约ID
     * @param timeRange 时间范围
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return K线数据列表
     */
    List<IBMarketHistoryData> selectByConidAndTimeRangeAndPeriod(@Param("conid") String conid,
                                                                 @Param("timeRange") String timeRange,
                                                                 @Param("startTime") LocalDateTime startTime,
                                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 删除过期的历史数据
     *
     * @param beforeTime 删除此时间之前的数据
     * @return 删除的记录数
     */
    int deleteExpiredData(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 批量插入历史数据
     *
     * @param dataList K线数据列表
     * @return 插入的记录数
     */
    int batchInsert(@Param("dataList") List<IBMarketHistoryData> dataList);
} 