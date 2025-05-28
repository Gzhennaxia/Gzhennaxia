export default {
  namespaced: true,
  state: {
    todos: [],
    categories: [],
    taskProgress: []
  },
  mutations: {
    SET_TODOS(state, todos) {
      state.todos = todos
    },
    ADD_TODO(state, todo) {
      state.todos.push(todo)
    },
    UPDATE_TODO(state, { index, todo }) {
      state.todos[index] = todo
    },
    DELETE_TODO(state, index) {
      state.todos.splice(index, 1)
    },
    SET_CATEGORIES(state, categories) {
      state.categories = categories
    },
    ADD_CATEGORY(state, category) {
      state.categories.push(category)
    },
    UPDATE_CATEGORY(state, { index, category }) {
      state.categories[index] = category
    },
    DELETE_CATEGORY(state, index) {
      state.categories.splice(index, 1)
    },
    SET_TASK_PROGRESS(state, progress) {
      state.taskProgress = progress
    },
    UPDATE_TASK_PROGRESS(state, { taskId, progress }) {
      const index = state.taskProgress.findIndex(p => p.taskId === taskId)
      if (index !== -1) {
        state.taskProgress[index] = { ...state.taskProgress[index], ...progress }
      } else {
        state.taskProgress.push({ taskId, ...progress })
      }
    }
  },
  actions: {
    async fetchTodos({ commit }) {
      try {
        // TODO: 实现获取待办事项的API调用
        const todos = []
        commit('SET_TODOS', todos)
      } catch (error) {
        console.error('获取待办事项失败:', error)
      }
    },
    async addTodo({ commit }, todo) {
      try {
        // TODO: 实现添加待办事项的API调用
        commit('ADD_TODO', todo)
      } catch (error) {
        console.error('添加待办事项失败:', error)
      }
    },
    async fetchCategories({ commit }) {
      try {
        // TODO: 实现获取任务分类的API调用
        const categories = []
        commit('SET_CATEGORIES', categories)
      } catch (error) {
        console.error('获取任务分类失败:', error)
      }
    },
    async addCategory({ commit }, category) {
      try {
        // TODO: 实现添加任务分类的API调用
        commit('ADD_CATEGORY', category)
      } catch (error) {
        console.error('添加任务分类失败:', error)
      }
    },
    async updateTaskProgress({ commit }, { taskId, progress }) {
      try {
        // TODO: 实现更新任务进度的API调用
        commit('UPDATE_TASK_PROGRESS', { taskId, progress })
      } catch (error) {
        console.error('更新任务进度失败:', error)
      }
    }
  },
  getters: {
    todosByCategory: state => categoryId => {
      return state.todos.filter(todo => todo.categoryId === categoryId)
    },
    completedTodos: state => {
      return state.todos.filter(todo => todo.completed)
    },
    incompleteTodos: state => {
      return state.todos.filter(todo => !todo.completed)
    },
    taskProgressByCategory: state => categoryId => {
      const categoryTasks = state.todos.filter(todo => todo.categoryId === categoryId)
      const completedTasks = categoryTasks.filter(todo => todo.completed).length
      return {
        total: categoryTasks.length,
        completed: completedTasks,
        percentage: categoryTasks.length > 0 ? (completedTasks / categoryTasks.length) * 100 : 0
      }
    }
  }
}