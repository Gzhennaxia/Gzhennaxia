import apiClient from '../utils/apiClient';
import type {
  AccCategory,
  AccChannel,
  AccTag,
  AccTransaction,
  AccTransactionImportResult,
  AccTransactionVO,
  DashboardVO,
  FundAccount,
  MonthlyReportVO,
  PageResult,
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

  saveCategory: (data: AccCategory) =>
    apiClient.post<unknown, boolean>('/accounting/categories', data),

  updateCategory: (data: AccCategory) =>
    apiClient.put<unknown, boolean>('/accounting/categories', data),

  deleteCategory: (id: number) =>
    apiClient.delete<unknown, boolean>(`/accounting/categories/${id}`),

  listChannels: () => apiClient.get<unknown, AccChannel[]>('/accounting/channels'),

  saveChannel: (data: Pick<AccChannel, 'name'>) =>
    apiClient.post<unknown, number>('/accounting/channels', data),

  updateChannel: (data: AccChannel) => apiClient.put<unknown, boolean>('/accounting/channels', data),

  deleteChannel: (id: number) => apiClient.delete<unknown, boolean>(`/accounting/channels/${id}`),

  listTags: () => apiClient.get<unknown, AccTag[]>('/accounting/tags'),

  saveTag: (data: Pick<AccTag, 'name' | 'color'>) =>
    apiClient.post<unknown, number>('/accounting/tags', data),

  /** 记一笔等场景快速创建（名称已存在则返回已有 ID） */
  quickSaveTag: (data: Pick<AccTag, 'name'>) =>
    apiClient.post<unknown, number>('/accounting/tags/quick', data),

  updateTag: (data: AccTag) => apiClient.put<unknown, boolean>('/accounting/tags', data),

  deleteTag: (id: number) => apiClient.delete<unknown, boolean>(`/accounting/tags/${id}`),

  pageTransactions: (params: Record<string, unknown>) =>
    apiClient.get<unknown, PageResult<AccTransactionVO>>('/accounting/transactions', { params }),

  saveTransaction: (data: AccTransaction) =>
    apiClient.post<unknown, number>('/accounting/transactions', data),

  updateTransaction: (data: AccTransaction) =>
    apiClient.put<unknown, boolean>('/accounting/transactions', data),

  deleteTransaction: (id: number) =>
    apiClient.delete<unknown, boolean>(`/accounting/transactions/${id}`),

  importTransactions: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post<unknown, AccTransactionImportResult>('/accounting/transactions/import', formData, {
      timeout: 120000,
    });
  },
};
