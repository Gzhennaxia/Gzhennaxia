import request from '@/utils/request'

// IB数据接口
export function getAccountInfo() {
  return request({
    url: '/api/ib/account',
    method: 'get'
  })
}

export function getPortfolio() {
  return request({
    url: '/api/ib/portfolio',
    method: 'get'
  })
}

export function getPositions() {
  return request({
    url: '/api/ib/positions',
    method: 'get'
  })
}

export function getTradeHistory() {
  return request({
    url: '/api/ib/trades',
    method: 'get'
  })
}

// 分析接口
export function getAssetAllocation() {
  return request({
    url: '/api/ib/analysis/allocation',
    method: 'get'
  })
}

export function getPerformanceAnalysis() {
  return request({
    url: '/api/ib/analysis/performance',
    method: 'get'
  })
}

export function getRiskMetrics() {
  return request({
    url: '/api/ib/analysis/risk',
    method: 'get'
  })
}