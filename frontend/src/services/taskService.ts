import axios from 'axios';
import { Task, TaskFormData } from '../types/Task';

const API_BASE_URL = '/api/tasks';

export const taskService = {
  // 获取所有任务
  getAllTasks: async (): Promise<Task[]> => {
    const response = await axios.get(API_BASE_URL);
    return response.data;
  },

  // 根据ID获取任务
  getTaskById: async (id: number): Promise<Task> => {
    const response = await axios.get(`${API_BASE_URL}/${id}`);
    return response.data;
  },

  // 创建任务
  createTask: async (task: TaskFormData): Promise<Task> => {
    const response = await axios.post(API_BASE_URL, task);
    return response.data;
  },

  // 更新任务
  updateTask: async (id: number, task: TaskFormData): Promise<Task> => {
    const response = await axios.put(`${API_BASE_URL}/${id}`, task);
    return response.data;
  },

  // 删除任务
  deleteTask: async (id: number): Promise<void> => {
    await axios.delete(`${API_BASE_URL}/${id}`);
  },

  // 根据日期范围获取任务
  getTasksByDateRange: async (startDate: string, endDate: string): Promise<Task[]> => {
    const response = await axios.get(`${API_BASE_URL}/date-range`, {
      params: { startDate, endDate }
    });
    return response.data;
  },

  // 根据日期获取任务
  getTasksByDate: async (date: string): Promise<Task[]> => {
    const response = await axios.get(`${API_BASE_URL}/date`, {
      params: { date }
    });
    return response.data;
  },

  // 根据状态获取任务
  getTasksByStatus: async (status: string): Promise<Task[]> => {
    const response = await axios.get(`${API_BASE_URL}/status/${status}`);
    return response.data;
  },

  // 根据分类获取任务
  getTasksByCategory: async (category: string): Promise<Task[]> => {
    const response = await axios.get(`${API_BASE_URL}/category/${category}`);
    return response.data;
  },

  // 获取任务统计信息
  getTaskStats: async (): Promise<{
    overdue: number;
    today: number;
    completed: number;
    total: number;
  }> => {
    const response = await axios.get(`${API_BASE_URL}/stats`);
    return response.data;
  }
};