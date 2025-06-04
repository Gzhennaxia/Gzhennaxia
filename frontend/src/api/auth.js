import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const api = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('Admin-Token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    if (code === 200) {
      // 只在登录成功时显示成功消息
      if (response.config.url === '/auth/login') {
        ElMessage.success('登录成功')
      }
      return response.data
    } else {
      // 显示错误消息
      ElMessage.error(message || '请求失败')
      // 统一处理错误提示
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  error => {
    // 处理 HTTP 错误状态
    let message = '请求失败'
    if (error.response) {
      const { status, data } = error.response
      // 优先使用后端返回的错误信息
      if (data && data.message) {
        message = data.message
      } else {
        switch (status) {
          case 401:
            message = '未授权，请重新登录'
            // 清除token并跳转到登录页
            localStorage.removeItem('token')
            router.push('/login')
            break
          case 403:
            message = '拒绝访问'
            break
          case 404:
            message = '请求错误，未找到该资源'
            break
          case 500:
            message = '服务器内部错误'
            break
          default:
            message = `请求失败(${status})`
        }
      }
    } else if (error.message) {
      message = error.message
    } else {
      message = '网络连接异常'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export const login = async (data) => {
  const response = await api.post('/auth/login', data)
  if (response.token) {
    localStorage.setItem('token', response.token)
  }
  return response
}

export const logout = async () => {
  const response = await api.post('/auth/logout')
  localStorage.removeItem('token')
  return response
}

export const getUserInfo = () => {
  return api.get('/auth/user')
}

export const register = async (data) => {
  return api.post('/auth/register', data)
}