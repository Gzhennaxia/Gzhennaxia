import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Root',
    redirect: to => {
      return store.state.user ? '/dashboard' : '/login'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/index.vue')
  },
  {
    path: '/finance',
    name: 'Finance',
    redirect: '/finance/portfolio',
    children: [
      {
        path: 'transactions',
        name: 'Transactions',
        component: () => import('@/views/finance/transactions.vue')
      },
      {
        path: 'budget',
        name: 'Budget',
        component: () => import('@/views/finance/budget.vue')
      },
      {
        path: 'investment',
        name: 'Investment',
        component: () => import('@/views/finance/investment.vue')
      },
      {
        path: 'portfolio',
        name: 'Portfolio', 
        component: () => import('@/views/finance/portfolio/index.vue'),
        children: [
          {
            path: 'stock/:id',
            name: 'StockDetail',
            component: () => import('@/views/finance/portfolio/stock-detail.vue')
          }
        ]
      }
    ]
  },
  {
    path: '/work',
    name: 'Work',
    children: [
      {
        path: 'projects',
        name: 'Projects',
        component: () => import('@/views/work/projects.vue')
      },
      {
        path: 'meetings',
        name: 'Meetings',
        component: () => import('@/views/work/meetings.vue')
      },
      {
        path: 'logs',
        name: 'WorkLogs',
        component: () => import('@/views/work/logs.vue')
      }
    ]
  },
  {
    path: '/career',
    name: 'Career',
    children: [
      {
        path: 'skills',
        name: 'Skills',
        component: () => import('@/views/career/skills.vue')
      },
      {
        path: 'learning',
        name: 'Learning',
        component: () => import('@/views/career/learning.vue')
      },
      {
        path: 'goals',
        name: 'Goals',
        component: () => import('@/views/career/goals.vue')
      }
    ]
  },
  {
    path: '/time',
    name: 'Time',
    children: [
      {
        path: 'schedule',
        name: 'Schedule',
        component: () => import('@/views/time/schedule.vue')
      },
      {
        path: 'tracking',
        name: 'TimeTracking',
        component: () => import('@/views/time/tracking.vue')
      },
      {
        path: 'habits',
        name: 'Habits',
        component: () => import('@/views/time/habits.vue')
      }
    ]
  },
  {
    path: '/tasks',
    name: 'Tasks',
    children: [
      {
        path: 'todo',
        name: 'Todo',
        component: () => import('@/views/tasks/todo.vue')
      },
      {
        path: 'categories',
        name: 'TaskCategories',
        component: () => import('@/views/tasks/categories.vue')
      },
      {
        path: 'progress',
        name: 'TaskProgress',
        component: () => import('@/views/tasks/progress.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isPublic = to.matched.some(record => record.meta.public)
  const isAuthenticated = store.state.user !== null

  if (!isPublic && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router