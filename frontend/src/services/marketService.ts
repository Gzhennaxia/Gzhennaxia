import apiClient from '../utils/apiClient';
import type { MarketDailyChartVO, MarketSyncResultVO } from '../types/Market';

/**
 * 理财模块行情 API（标普 500 等）。
 */
export const marketService = {
  getDaily: (symbol: string, startDate: string, endDate: string) =>
    apiClient.get<unknown, MarketDailyChartVO>('/finance/market/daily', {
      params: { symbol, startDate, endDate },
    }),

  sync: (symbol: string) =>
    apiClient.post<unknown, MarketSyncResultVO>(`/finance/market/sync/${symbol}`),
};
