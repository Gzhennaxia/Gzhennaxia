import apiClient from '../utils/apiClient';
import { InboxTask, InboxTaskFormData } from '../types/InboxTask';

const API_BASE_URL = '/inboxTask';

export const inboxTaskService = {
  // 获取所有收集箱任务
  getAllInboxTasks: async (): Promise<InboxTask[]> => {
    return await apiClient.get(API_BASE_URL);
  },

  // 创建收集箱任务
  createInboxTask: async (task: InboxTaskFormData): Promise<InboxTask> => {
    return await apiClient.post(API_BASE_URL, task);
  },

  // 更新收集箱任务
  updateInboxTask: async (id: number, task: InboxTaskFormData): Promise<InboxTask> => {
    return await apiClient.put(`${API_BASE_URL}/${id}`, task);
  },

  // 删除收集箱任务
  deleteInboxTask: async (id: number): Promise<void> => {
    await apiClient.delete(`${API_BASE_URL}/${id}`);
  },

  // 获取收集箱任务数量
  getInboxTaskCount: async (): Promise<number> => {
    return await apiClient.get(`${API_BASE_URL}/count`);
  }
};