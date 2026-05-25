import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
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
  (config: InternalAxiosRequestConfig) => {
    if (config.data instanceof FormData && config.headers) {
      delete config.headers['Content-Type'];
    }
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
    const { code, message, data } = response.data;
    if (code !== 200) {
      return Promise.reject({ code, message });
    }
    return data; // 直接返回data字段
  },
  (error) => {
    // 统一处理错误
    if (error.response) {
      const { code, message } = error.response.data;
      return Promise.reject({ code, message });
    }
    return Promise.reject({
      code: -1,
      message: error.message || '网络错误'
    });
  }
);

export default apiClient;