import router from './index'
import store from '@/store'

router.beforeEach((to, from, next) => {
  // 获取用户登录状态
  const hasToken = store.getters.token
  const isPublicRoute = to.meta.public

  if (hasToken) {
    if (to.path === '/login') {
      // 已登录用户访问登录页，重定向到首页
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    if (isPublicRoute) {
      // 未登录用户访问公共页面
      next()
    } else {
      // 未登录用户访问其他页面，重定向到登录页
      next(`/login?redirect=${to.path}`)
    }
  }
})