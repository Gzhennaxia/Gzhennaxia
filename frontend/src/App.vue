<template>
  <el-container class="app-container" v-if="store.state.user">
    <el-aside width="200px">
      <el-menu
        class="el-menu-vertical"
        :default-active="$route.path"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-sub-menu index="/finance">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>财务管理</span>
          </template>
          <el-menu-item index="/finance/transactions">交易记录</el-menu-item>
          <el-menu-item index="/finance/budget">预算管理</el-menu-item>
          <el-menu-item index="/finance/investment">投资管理</el-menu-item>
          <el-menu-item index="/finance/portfolio">投资组合</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/work">
          <template #title>
            <el-icon><Briefcase /></el-icon>
            <span>工作管理</span>
          </template>
          <el-menu-item index="/work/projects">项目管理</el-menu-item>
          <el-menu-item index="/work/meetings">会议管理</el-menu-item>
          <el-menu-item index="/work/logs">工作日志</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/career">
          <template #title>
            <el-icon><Trophy /></el-icon>
            <span>职业发展</span>
          </template>
          <el-menu-item index="/career/skills">技能管理</el-menu-item>
          <el-menu-item index="/career/learning">学习计划</el-menu-item>
          <el-menu-item index="/career/goals">目标设定</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/time">
          <template #title>
            <el-icon><Timer /></el-icon>
            <span>时间管理</span>
          </template>
          <el-menu-item index="/time/schedule">日程安排</el-menu-item>
          <el-menu-item index="/time/tracking">时间追踪</el-menu-item>
          <el-menu-item index="/time/habits">习惯养成</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/tasks">
          <template #title>
            <el-icon><List /></el-icon>
            <span>任务管理</span>
          </template>
          <el-menu-item index="/tasks/todo">待办事项</el-menu-item>
          <el-menu-item index="/tasks/categories">任务分类</el-menu-item>
          <el-menu-item index="/tasks/progress">任务进度</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header height="60px">
        <div class="header-content">
          <div></div>
          <el-dropdown @command="handleLogout">
            <span class="el-dropdown-link">
              {{ store.state.user.username }}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
  <router-view v-else></router-view>
</template>

<script setup>
import { DataLine, Money, Briefcase, Trophy, Timer, List } from '@element-plus/icons-vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

const handleLogout = async () => {
  await store.dispatch('logout')
  router.push('/login')
}
</script>

<style scoped>
.app-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.el-menu-vertical {
  border-right: none;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 20px;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #606266;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>