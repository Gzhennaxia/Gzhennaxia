<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <h3>欢迎回来</h3>
              <el-tag>{{ currentTime }}</el-tag>
            </div>
          </template>
          <p>今天是新的一天，让我们开始工作吧！</p>
        </el-card>
      </el-col>

      <!-- 模块概览卡片 -->
      <el-col :span="6" v-for="module in modules" :key="module.name">
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <h4>{{ module.title }}</h4>
              <el-button text @click="navigateTo(module.path)">查看详情</el-button>
            </div>
          </template>
          <div class="module-stats">
            <el-statistic :title="stat.title" :value="stat.value" v-for="stat in module.stats" :key="stat.title" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

// 获取当前时间
const currentTime = computed(() => {
  const now = new Date()
  return now.toLocaleString('zh-CN', { hour12: false })
})

// 模块数据
const modules = ref([
  {
    name: 'finance',
    title: '财务管理',
    path: '/finance/transactions',
    stats: [
      { title: '本月支出', value: '¥3,500' },
      { title: '预算使用率', value: '65%' }
    ]
  },
  {
    name: 'work',
    title: '工作管理',
    path: '/work/projects',
    stats: [
      { title: '进行中项目', value: 5 },
      { title: '待办会议', value: 2 }
    ]
  },
  {
    name: 'career',
    title: '职业发展',
    path: '/career/goals',
    stats: [
      { title: '技能目标', value: '8/10' },
      { title: '学习进度', value: '75%' }
    ]
  },
  {
    name: 'tasks',
    title: '任务管理',
    path: '/tasks/todo',
    stats: [
      { title: '待办任务', value: 12 },
      { title: '完成率', value: '80%' }
    ]
  }
])

// 导航到指定路径
const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3,
.card-header h4 {
  margin: 0;
}

.module-card {
  margin-bottom: 20px;
}

.module-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}
</style>