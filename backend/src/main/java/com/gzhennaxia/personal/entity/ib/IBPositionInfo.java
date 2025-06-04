package com.gzhennaxia.personal.entity.ib;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 持仓信息
 */
@Data
@TableName("ib_position_info")
public class IBPositionInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账户ID
     */
    private String accountId;

    /**
     * 合约ID
     */
    private String conid;

    /**
     * 合约描述
     */
    private String contractDesc;

    /**
     * 持仓数量
     */
    private BigDecimal position;

    /**
     * 市场价格
     */
    private BigDecimal mktPrice;

    /**
     * 市场价值
     */
    private BigDecimal mktValue;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 平均成本
     */
    private BigDecimal avgCost;

    /**
     * 平均价格
     */
    private BigDecimal avgPrice;

    /**
     * 已实现盈亏
     */
    private BigDecimal realizedPnl;

    /**
     * 未实现盈亏
     */
    private BigDecimal unrealizedPnl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 收益率
     */
    @TableField(exist = false)
    private BigDecimal returnRate;

}