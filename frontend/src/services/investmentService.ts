import apiClient from '../utils/apiClient';
import type { InvestmentSymbol, InvestmentTrade, PositionVO } from '../types/Accounting';

/**
 * 投资管理子系统 API（后端仍挂在 /api/accounting 路径下）。
 */
export const investmentService = {
  listSymbols: () => apiClient.get<unknown, InvestmentSymbol[]>('/accounting/symbols'),

  saveSymbol: (data: InvestmentSymbol) =>
    apiClient.post<unknown, boolean>('/accounting/symbols', data),

  listPositions: () => apiClient.get<unknown, PositionVO[]>('/accounting/positions'),

  listInvestmentTrades: () =>
    apiClient.get<unknown, InvestmentTrade[]>('/accounting/investment-trades'),

  saveInvestmentTrade: (data: InvestmentTrade) =>
    apiClient.post<unknown, number>('/accounting/investment-trades', data),

  deleteInvestmentTrade: (id: number) =>
    apiClient.delete<unknown, boolean>(`/accounting/investment-trades/${id}`),
};
