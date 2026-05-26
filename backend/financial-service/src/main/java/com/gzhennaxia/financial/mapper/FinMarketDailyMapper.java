package com.gzhennaxia.financial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.financial.pojo.entity.FinMarketDaily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 行情日 K Mapper。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Mapper
public interface FinMarketDailyMapper extends BaseMapper<FinMarketDaily> {

    /**
     * 查询区间内日 K，按日期升序。
     */
    @Select("SELECT id, symbol_id, trade_date, open_price, high_price, low_price, close_price, volume, source, "
            + "create_time, update_time FROM fin_market_daily "
            + "WHERE symbol_id = #{symbolId} AND trade_date >= #{startDate} AND trade_date <= #{endDate} "
            + "ORDER BY trade_date ASC")
    List<FinMarketDaily> selectRange(@Param("symbolId") Long symbolId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    /**
     * 库内最大交易日。
     */
    @Select("SELECT MAX(trade_date) FROM fin_market_daily WHERE symbol_id = #{symbolId}")
    LocalDate selectMaxTradeDate(@Param("symbolId") Long symbolId);

    /**
     * PostgreSQL upsert 单条日 K。
     */
    void upsertOne(FinMarketDaily daily);
}
