import { createStore } from 'vuex'
import { login, logout } from '@/api/auth'
import { setToken, removeToken } from '@/utils/auth'

// 导入各个模块的状态管理
import finance from './modules/finance'
import work from './modules/work'
import career from './modules/career'
import time from './modules/time'
import tasks from './modules/tasks'

export default createStore({
  state: {
    user: null,
    token: '',
    theme: 'light',
    sidebar: {
      opened: true
    }
  },
  getters: {
    token: state => state.token
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    SET_TOKEN(state, token) {
      state.token = token
      setToken(token)
    },
    SET_THEME(state, theme) {
      state.theme = theme
    },
    TOGGLE_SIDEBAR(state) {
      state.sidebar.opened = !state.sidebar.opened
    }
  },
  actions: {
    async login({ commit }, userInfo) {
      try {
        const { data } = await login(userInfo)
        commit('SET_TOKEN', data.token)
        commit('SET_USER', data.user)
        return data
      } catch (error) {
        throw new Error(error.response?.data?.message || '登录失败')
      }
    },
    async logout({ commit }) {
      try {
        await logout()
        commit('SET_TOKEN', '')
        commit('SET_USER', null)
        removeToken()
      } catch (error) {
        console.error('登出失败:', error)
      }
    }
  },
  modules: {
    finance,
    work,
    career,
    time,
    tasks
  }
})