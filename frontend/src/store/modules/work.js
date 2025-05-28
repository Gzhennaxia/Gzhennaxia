export default {
  namespaced: true,
  state: {
    projects: [],
    meetings: [],
    workLogs: []
  },
  mutations: {
    SET_PROJECTS(state, projects) {
      state.projects = projects
    },
    ADD_PROJECT(state, project) {
      state.projects.push(project)
    },
    UPDATE_PROJECT(state, { index, project }) {
      state.projects[index] = project
    },
    DELETE_PROJECT(state, index) {
      state.projects.splice(index, 1)
    },
    SET_MEETINGS(state, meetings) {
      state.meetings = meetings
    },
    ADD_MEETING(state, meeting) {
      state.meetings.push(meeting)
    },
    UPDATE_MEETING(state, { index, meeting }) {
      state.meetings[index] = meeting
    },
    DELETE_MEETING(state, index) {
      state.meetings.splice(index, 1)
    },
    SET_WORK_LOGS(state, logs) {
      state.workLogs = logs
    },
    ADD_WORK_LOG(state, log) {
      state.workLogs.push(log)
    }
  },
  actions: {
    async fetchProjects({ commit }) {
      try {
        // TODO: 实现获取项目列表的API调用
        const projects = []
        commit('SET_PROJECTS', projects)
      } catch (error) {
        console.error('获取项目列表失败:', error)
      }
    },
    async addProject({ commit }, project) {
      try {
        // TODO: 实现添加项目的API调用
        commit('ADD_PROJECT', project)
      } catch (error) {
        console.error('添加项目失败:', error)
      }
    },
    async fetchMeetings({ commit }) {
      try {
        // TODO: 实现获取会议列表的API调用
        const meetings = []
        commit('SET_MEETINGS', meetings)
      } catch (error) {
        console.error('获取会议列表失败:', error)
      }
    },
    async addMeeting({ commit }, meeting) {
      try {
        // TODO: 实现添加会议的API调用
        commit('ADD_MEETING', meeting)
      } catch (error) {
        console.error('添加会议失败:', error)
      }
    },
    async addWorkLog({ commit }, log) {
      try {
        // TODO: 实现添加工作日志的API调用
        commit('ADD_WORK_LOG', log)
      } catch (error) {
        console.error('添加工作日志失败:', error)
      }
    }
  },
  getters: {
    ongoingProjects: state => {
      return state.projects.filter(p => p.status === 'ongoing')
    },
    upcomingMeetings: state => {
      const now = new Date()
      return state.meetings
        .filter(m => new Date(m.startTime) > now)
        .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
    },
    recentWorkLogs: state => {
      return state.workLogs
        .sort((a, b) => new Date(b.date) - new Date(a.date))
        .slice(0, 10)
    }
  }
}