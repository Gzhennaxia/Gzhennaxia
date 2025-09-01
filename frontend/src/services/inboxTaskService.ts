import axios from 'axios';
import { InboxTask, InboxTaskFormData } from '../types/InboxTask';

const API_BASE_URL = 'http://localhost:8080/api/inbox-tasks';

export const inboxTaskService = {
  // 获取所有收集箱任务
  getAllInboxTasks: async (): Promise<InboxTask[]> => {
    const response = await axios.get(API_BASE_URL);
    return response.data;
  },

  // 创建收集箱任务
  createInboxTask: async (task: InboxTaskFormData): Promise<InboxTask> => {
    const response = await axios.post(API_BASE_URL, task);
    return response.data;
  },

  // 更新收集箱任务
  updateInboxTask: async (id: number, task: InboxTaskFormData): Promise<InboxTask> => {
    const response = await axios.put(`${API_BASE_URL}/${id}`, task);
    return response.data;
  },

  // 删除收集箱任务
  deleteInboxTask: async (id: number): Promise<void> => {
    await axios.delete(`${API_BASE_URL}/${id}`);
  },

  // 获取收集箱任务数量
  getInboxTaskCount: async (): Promise<number> => {
    const response = await axios.get(`${API_BASE_URL}/count`);
    return response.data;
  }
};