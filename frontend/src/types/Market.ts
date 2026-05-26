/** 行情折线图数据点 */
export interface MarketDailyPoint {
  date: string;
  close: number;
}

/** 行情折线图响应 */
export interface MarketDailyChartVO {
  symbol: string;
  name: string;
  currency: string;
  points: MarketDailyPoint[];
}

/** 行情同步结果 */
export interface MarketSyncResultVO {
  symbol: string;
  rowsAffected: number;
  startDate: string;
  endDate: string;
  status: string;
  message: string;
}
