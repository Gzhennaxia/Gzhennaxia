export default {
  namespaced: true,
  state: {
    transactions: [],
    budget: {
      monthly: {},
      categories: []
    },
    investments: []
  },
  mutations: {
    SET_TRANSACTIONS(state, transactions) {
      state.transactions = transactions
    },
    ADD_TRANSACTION(state, transaction) {
      state.transactions.push(transaction)
    },
    UPDATE_TRANSACTION(state, { index, transaction }) {
      state.transactions[index] = transaction
    },
    DELETE_TRANSACTION(state, index) {
      state.transactions.splice(index, 1)
    },
    SET_BUDGET(state, budget) {
      state.budget = budget
    },
    SET_INVESTMENTS(state, investments) {
      state.investments = investments
    }
  },
  actions: {
    async fetchTransactions({ commit }) {
      try {
        // TODO: 实现获取交易记录的API调用
        const transactions = []
        commit('SET_TRANSACTIONS', transactions)
      } catch (error) {
        console.error('获取交易记录失败:', error)
      }
    },
    async addTransaction({ commit }, transaction) {
      try {
        // TODO: 实现添加交易记录的API调用
        commit('ADD_TRANSACTION', transaction)
      } catch (error) {
        console.error('添加交易记录失败:', error)
      }
    },
    async updateBudget({ commit }, budget) {
      try {
        // TODO: 实现更新预算的API调用
        commit('SET_BUDGET', budget)
      } catch (error) {
        console.error('更新预算失败:', error)
      }
    },
    async fetchInvestments({ commit }) {
      try {
        // TODO: 实现获取投资信息的API调用
        const investments = []
        commit('SET_INVESTMENTS', investments)
      } catch (error) {
        console.error('获取投资信息失败:', error)
      }
    }
  },
  getters: {
    totalIncome: state => {
      return state.transactions
        .filter(t => t.type === 'income')
        .reduce((sum, t) => sum + t.amount, 0)
    },
    totalExpense: state => {
      return state.transactions
        .filter(t => t.type === 'expense')
        .reduce((sum, t) => sum + t.amount, 0)
    },
    balance: (state, getters) => {
      return getters.totalIncome - getters.totalExpense
    },
    budgetStatus: state => {
      // TODO: 实现预算状态的计算逻辑
      return state.budget
    },
    investmentPerformance: state => {
      // TODO: 实现投资表现的计算逻辑
      return state.investments
    }
  }
}