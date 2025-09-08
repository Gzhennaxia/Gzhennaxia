import apiClient from '../utils/apiClient';
import { Task, TaskFormData } from '../types/Task';

const API_BASE_URL = '/task';

export const taskService = {
  // 获取所有任务
  getAllTasks: async (): Promise<Task[]> => {
    return await apiClient.get(API_BASE_URL);
  },

  // 根据ID获取任务
  getTaskById: async (id: number): Promise<Task> => {
    return await apiClient.get(`${API_BASE_URL}/${id}`);
  },

  // 创建任务
  createTask: async (task: TaskFormData): Promise<Task> => {
    return await apiClient.post(API_BASE_URL, task);
  },

  // 更新任务
  updateTask: async (id: number, task: TaskFormData): Promise<Task> => {
    return await apiClient.put(`${API_BASE_URL}/${id}`, task);
  },

  // 删除任务
  deleteTask: async (id: number): Promise<void> => {
    await apiClient.delete(`${API_BASE_URL}/${id}`);
  },

  // 根据日期范围获取任务
  getTasksByDateRange: async (startDate: string, endDate: string): Promise<Task[]> => {
    return await apiClient.get(`${API_BASE_URL}/date-range`, {
      params: { startDate, endDate }
    });
  },

  // 根据日期获取任务
  getTasksByDate: async (date: string): Promise<Task[]> => {
    return await apiClient.get(`${API_BASE_URL}/date`, {
      params: { date }
    });
  },

  // 根据状态获取任务
  getTasksByStatus: async (status: string): Promise<Task[]> => {
    return await apiClient.get(`${API_BASE_URL}/status/${status}`);
  },

  // 根据分类获取任务
  getTasksByCategory: async (category: string): Promise<Task[]> => {
    return await apiClient.get(`${API_BASE_URL}/category/${category}`);
  },

  // 获取任务统计信息
  getTaskStats: async (): Promise<{
    overdue: number;
    today: number;
    completed: number;
    total: number;
  }> => {
    return await apiClient.get(`${API_BASE_URL}/stats`);
  }
};