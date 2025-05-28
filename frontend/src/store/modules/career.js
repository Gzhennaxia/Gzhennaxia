export default {
  namespaced: true,
  state: {
    skills: [],
    learningItems: [],
    goals: []
  },
  mutations: {
    SET_SKILLS(state, skills) {
      state.skills = skills
    },
    ADD_SKILL(state, skill) {
      state.skills.push(skill)
    },
    UPDATE_SKILL(state, { index, skill }) {
      state.skills[index] = skill
    },
    DELETE_SKILL(state, index) {
      state.skills.splice(index, 1)
    },
    SET_LEARNING_ITEMS(state, items) {
      state.learningItems = items
    },
    ADD_LEARNING_ITEM(state, item) {
      state.learningItems.push(item)
    },
    UPDATE_LEARNING_ITEM(state, { index, item }) {
      state.learningItems[index] = item
    },
    SET_GOALS(state, goals) {
      state.goals = goals
    },
    ADD_GOAL(state, goal) {
      state.goals.push(goal)
    },
    UPDATE_GOAL(state, { index, goal }) {
      state.goals[index] = goal
    },
    DELETE_GOAL(state, index) {
      state.goals.splice(index, 1)
    }
  },
  actions: {
    async fetchSkills({ commit }) {
      try {
        // TODO: 实现获取技能列表的API调用
        const skills = []
        commit('SET_SKILLS', skills)
      } catch (error) {
        console.error('获取技能列表失败:', error)
      }
    },
    async addSkill({ commit }, skill) {
      try {
        // TODO: 实现添加技能的API调用
        commit('ADD_SKILL', skill)
      } catch (error) {
        console.error('添加技能失败:', error)
      }
    },
    async fetchLearningItems({ commit }) {
      try {
        // TODO: 实现获取学习项目的API调用
        const items = []
        commit('SET_LEARNING_ITEMS', items)
      } catch (error) {
        console.error('获取学习项目失败:', error)
      }
    },
    async addLearningItem({ commit }, item) {
      try {
        // TODO: 实现添加学习项目的API调用
        commit('ADD_LEARNING_ITEM', item)
      } catch (error) {
        console.error('添加学习项目失败:', error)
      }
    },
    async fetchGoals({ commit }) {
      try {
        // TODO: 实现获取目标列表的API调用
        const goals = []
        commit('SET_GOALS', goals)
      } catch (error) {
        console.error('获取目标列表失败:', error)
      }
    },
    async addGoal({ commit }, goal) {
      try {
        // TODO: 实现添加目标的API调用
        commit('ADD_GOAL', goal)
      } catch (error) {
        console.error('添加目标失败:', error)
      }
    }
  },
  getters: {
    skillsByCategory: state => category => {
      return state.skills.filter(s => s.category === category)
    },
    inProgressLearning: state => {
      return state.learningItems.filter(item => item.status === 'in-progress')
    },
    activeGoals: state => {
      return state.goals.filter(g => !g.completed)
    },
    completedGoals: state => {
      return state.goals.filter(g => g.completed)
    }
  }
}