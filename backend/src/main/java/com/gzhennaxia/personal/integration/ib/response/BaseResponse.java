package com.gzhennaxia.personal.integration.ib.response;

import lombok.Data;

/**
 * IBKR API 响应基类
 */
@Data
public class BaseResponse {
    /**
     * 账户 ID
     */
    private String id;

    /**
     * 账户 ID
     */
    private String accountId;

    /**
     * 货币类型
     */
    private String currency;
}