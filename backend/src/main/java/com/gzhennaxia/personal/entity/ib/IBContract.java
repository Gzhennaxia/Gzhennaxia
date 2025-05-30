package com.gzhennaxia.personal.entity.ib;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IB合约信息
 */
@Data
@Builder
@TableName("ib_contract")
public class IBContract {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合约名称
     */
    private String name;
//
//    /**
//     * 合约类型
//     */
//    private String type;
//
//    /**
//     * 交易所
//     */
//    private String exchange;
//
//    /**
//     * 货币类型
//     */
//    private String currency;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
