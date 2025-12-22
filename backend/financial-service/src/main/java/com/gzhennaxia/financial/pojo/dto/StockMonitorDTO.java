package com.gzhennaxia.financial.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StockMonitorDTO {
    private Long id;
    private String exchange;
    private String stockCode;
    private String stockName;
    private String currencySymbol;
    private Double price1yAgo;
    private Double rise1y;
    private Double price6mAgo;
    private Double rise6m;
    private Double price3mAgo;
    private Double rise3m;
    private Double price1mAgo;
    private Double rise1m;
    private Double price1wAgo;
    private Double rise1w;
    private Double price3dAgo;
    private Double rise3d;
    private Double priceYesterday;
    private Double riseYesterday;
    private Date cacheCreateTime;
    private Date cacheExpireTime;
    private Integer status;
    private String remark;
    private Integer deleted;
    private Date createdTime;
    private Date updatedTime;
}