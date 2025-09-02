import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { ApiResponse } from '../types/Task';

// 创建axios实例
const apiClient: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 可以在这里添加token等
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    // 统一处理响应数据
    if (response.data.code !== 200) {
      return Promise.reject(response.data);
    }
    return response.data.data;
  },
  (error) => {
    // 统一处理错误
    return Promise.reject(error);
  }
);

export default apiClient;