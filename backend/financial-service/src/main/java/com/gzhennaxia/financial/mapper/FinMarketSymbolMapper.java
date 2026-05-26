package com.gzhennaxia.financial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.financial.pojo.entity.FinMarketSymbol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 行情标的 Mapper。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Mapper
public interface FinMarketSymbolMapper extends BaseMapper<FinMarketSymbol> {

    /**
     * 按系统代码查询标的。
     *
     * @param symbol 系统代码，如 SP500
     * @return 标的，不存在则 null
     */
    @Select("SELECT id, symbol, name, market_type, currency, data_source, external_id, remark, create_time, update_time "
            + "FROM fin_market_symbol WHERE symbol = #{symbol} LIMIT 1")
    FinMarketSymbol selectBySymbol(@Param("symbol") String symbol);
}
