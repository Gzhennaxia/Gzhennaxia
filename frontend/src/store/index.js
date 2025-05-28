import { createStore } from 'vuex'
import { login, logout } from '@/api/auth'

// 导入各个模块的状态管理
import finance from './modules/finance'
import work from './modules/work'
import career from './modules/career'
import time from './modules/time'
import tasks from './modules/tasks'

export default createStore({
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    theme: 'light',
    sidebar: {
      opened: true
    }
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
        commit('SET_USER', data.user)
        return data
      } catch (error) {
        throw new Error(error.response?.data?.message || '登录失败')
      }
    },
    async logout({ commit }) {
      try {
        await logout()
        commit('SET_USER', null)
      } catch (error) {
        console.error('登出失败:', error)
      }
    },
    toggleTheme({ commit }, theme) {
      commit('SET_THEME', theme)
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