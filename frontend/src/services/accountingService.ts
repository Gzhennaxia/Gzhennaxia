import apiClient from '../utils/apiClient';
import type {
  AccCategory,
  AccTransaction,
  AccTransactionVO,
  DashboardVO,
  FundAccount,
  InvestmentSymbol,
  InvestmentTrade,
  MonthlyReportVO,
  PageResult,
  PositionVO,
} from '../types/Accounting';

export const accountingService = {
  getDashboard: (year?: number, month?: number) =>
    apiClient.get<unknown, DashboardVO>('/accounting/dashboard', { params: { year, month } }),

  getMonthlyReport: (year: number, month: number) =>
    apiClient.get<unknown, MonthlyReportVO>('/accounting/reports/monthly', { params: { year, month } }),

  listAccounts: () => apiClient.get<unknown, FundAccount[]>('/accounting/accounts'),

  saveAccount: (data: FundAccount) => apiClient.post<unknown, number>('/accounting/accounts', data),

  updateAccount: (data: FundAccount) => apiClient.put<unknown, boolean>('/accounting/accounts', data),

  deleteAccount: (id: number) => apiClient.delete<unknown, boolean>(`/accounting/accounts/${id}`),

  listCategories: (type?: string) =>
    apiClient.get<unknown, AccCategory[]>('/accounting/categories', { params: { type } }),

  pageTransactions: (params: Record<string, unknown>) =>
    apiClient.get<unknown, PageResult<AccTransactionVO>>('/accounting/transactions', { params }),

  saveTransaction: (data: AccTransaction) =>
    apiClient.post<unknown, number>('/accounting/transactions', data),

  updateTransaction: (data: AccTransaction) =>
    apiClient.put<unknown, boolean>('/accounting/transactions', data),

  deleteTransaction: (id: number) =>
    apiClient.delete<unknown, boolean>(`/accounting/transactions/${id}`),

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
