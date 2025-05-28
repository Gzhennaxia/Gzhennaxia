export default {
  namespaced: true,
  state: {
    schedules: [],
    timeTrackings: [],
    habits: []
  },
  mutations: {
    SET_SCHEDULES(state, schedules) {
      state.schedules = schedules
    },
    ADD_SCHEDULE(state, schedule) {
      state.schedules.push(schedule)
    },
    UPDATE_SCHEDULE(state, { index, schedule }) {
      state.schedules[index] = schedule
    },
    DELETE_SCHEDULE(state, index) {
      state.schedules.splice(index, 1)
    },
    SET_TIME_TRACKINGS(state, trackings) {
      state.timeTrackings = trackings
    },
    ADD_TIME_TRACKING(state, tracking) {
      state.timeTrackings.push(tracking)
    },
    SET_HABITS(state, habits) {
      state.habits = habits
    },
    ADD_HABIT(state, habit) {
      state.habits.push(habit)
    },
    UPDATE_HABIT(state, { index, habit }) {
      state.habits[index] = habit
    },
    DELETE_HABIT(state, index) {
      state.habits.splice(index, 1)
    },
    UPDATE_HABIT_PROGRESS(state, { habitId, date, completed }) {
      const habit = state.habits.find(h => h.id === habitId)
      if (habit) {
        if (!habit.progress) habit.progress = {}
        habit.progress[date] = completed
      }
    }
  },
  actions: {
    async fetchSchedules({ commit }) {
      try {
        // TODO: 实现获取日程安排的API调用
        const schedules = []
        commit('SET_SCHEDULES', schedules)
      } catch (error) {
        console.error('获取日程安排失败:', error)
      }
    },
    async addSchedule({ commit }, schedule) {
      try {
        // TODO: 实现添加日程的API调用
        commit('ADD_SCHEDULE', schedule)
      } catch (error) {
        console.error('添加日程失败:', error)
      }
    },
    async fetchTimeTrackings({ commit }) {
      try {
        // TODO: 实现获取时间追踪记录的API调用
        const trackings = []
        commit('SET_TIME_TRACKINGS', trackings)
      } catch (error) {
        console.error('获取时间追踪记录失败:', error)
      }
    },
    async addTimeTracking({ commit }, tracking) {
      try {
        // TODO: 实现添加时间追踪记录的API调用
        commit('ADD_TIME_TRACKING', tracking)
      } catch (error) {
        console.error('添加时间追踪记录失败:', error)
      }
    },
    async fetchHabits({ commit }) {
      try {
        // TODO: 实现获取习惯列表的API调用
        const habits = []
        commit('SET_HABITS', habits)
      } catch (error) {
        console.error('获取习惯列表失败:', error)
      }
    },
    async addHabit({ commit }, habit) {
      try {
        // TODO: 实现添加习惯的API调用
        commit('ADD_HABIT', habit)
      } catch (error) {
        console.error('添加习惯失败:', error)
      }
    },
    async updateHabitProgress({ commit }, { habitId, date, completed }) {
      try {
        // TODO: 实现更新习惯进度的API调用
        commit('UPDATE_HABIT_PROGRESS', { habitId, date, completed })
      } catch (error) {
        console.error('更新习惯进度失败:', error)
      }
    }
  },
  getters: {
    todaySchedules: state => {
      const today = new Date().toISOString().split('T')[0]
      return state.schedules.filter(s => s.date === today)
    },
    weeklyTimeTracking: state => {
      const oneWeekAgo = new Date()
      oneWeekAgo.setDate(oneWeekAgo.getDate() - 7)
      return state.timeTrackings.filter(t => new Date(t.date) >= oneWeekAgo)
    },
    activeHabits: state => {
      return state.habits.filter(h => h.active)
    },
    habitProgress: state => habitId => {
      const habit = state.habits.find(h => h.id === habitId)
      return habit ? habit.progress || {} : {}
    }
  }
}