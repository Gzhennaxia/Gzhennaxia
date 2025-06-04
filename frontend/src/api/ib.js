import request from '@/utils/request'

export function getAccountInfo() {
  return request({
    url: '/api/ib/account/info',
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
    url: '/api/ib/position/info',
    method: 'get'
  })
}

export function refreshPositions() {
  return request({
    url: '/api/ib/position/info/refresh',
    method: 'post'
  })
}

export function getStockInfo(conid) {
  return request({
    url: `/api/ib/position/info/${conid}`,
    method: 'get'
  })
}

export function getHistoricalData(conid, timeRange) {
  return request({
    url: `/api/ib/position/history/${conid}`,
    method: 'get',
    params: { timeRange }
  })
}

export function getAssetAllocation() {
  return request({
    url: '/api/ib/analysis/asset-allocation',
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