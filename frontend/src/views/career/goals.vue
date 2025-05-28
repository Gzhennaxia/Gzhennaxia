<template>
  <div class="career-goals">
    <el-row :gutter="20">
      <!-- 短期目标 -->
      <el-col :span="8">
        <el-card class="goal-card">
          <template #header>
            <div class="card-header">
              <h3>短期目标</h3>
              <el-button type="text" @click="addGoal('short')">添加</el-button>
            </div>
          </template>
          <div v-for="(goal, index) in shortTermGoals" :key="index" class="goal-item">
            <el-checkbox v-model="goal.completed" @change="updateProgress">{{ goal.content }}</el-checkbox>
            <div class="goal-date">截止日期: {{ goal.deadline }}</div>
          </div>
        </el-card>
      </el-col>

      <!-- 中期目标 -->
      <el-col :span="8">
        <el-card class="goal-card">
          <template #header>
            <div class="card-header">
              <h3>中期目标</h3>
              <el-button type="text" @click="addGoal('medium')">添加</el-button>
            </div>
          </template>
          <div v-for="(goal, index) in mediumTermGoals" :key="index" class="goal-item">
            <el-checkbox v-model="goal.completed" @change="updateProgress">{{ goal.content }}</el-checkbox>
            <div class="goal-date">截止日期: {{ goal.deadline }}</div>
          </div>
        </el-card>
      </el-col>

      <!-- 长期目标 -->
      <el-col :span="8">
        <el-card class="goal-card">
          <template #header>
            <div class="card-header">
              <h3>长期目标</h3>
              <el-button type="text" @click="addGoal('long')">添加</el-button>
            </div>
          </template>
          <div v-for="(goal, index) in longTermGoals" :key="index" class="goal-item">
            <el-checkbox v-model="goal.completed" @change="updateProgress">{{ goal.content }}</el-checkbox>
            <div class="goal-date">截止日期: {{ goal.deadline }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 目标进度概览 -->
    <el-card class="progress-card">
      <template #header>
        <div class="card-header">
          <h3>目标完成进度</h3>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <h4>短期目标</h4>
          <el-progress :percentage="shortTermProgress" />
        </el-col>
        <el-col :span="8">
          <h4>中期目标</h4>
          <el-progress :percentage="mediumTermProgress" />
        </el-col>
        <el-col :span="8">
          <h4>长期目标</h4>
          <el-progress :percentage="longTermProgress" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 添加目标对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="'添加' + goalTypeText + '目标'"
      width="50%"
    >
      <el-form :model="goalForm" label-width="120px">
        <el-form-item label="目标内容">
          <el-input v-model="goalForm.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="goalForm.deadline"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGoal">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 示例数据
const shortTermGoals = ref([
  { content: '完成Vue 3项目重构', deadline: '2024-03-31', completed: true },
  { content: '参加技术分享会', deadline: '2024-04-15', completed: false }
])

const mediumTermGoals = ref([
  { content: '获取高级工程师认证', deadline: '2024-06-30', completed: false },
  { content: '带领团队完成重点项目', deadline: '2024-09-30', completed: false }
])

const longTermGoals = ref([
  { content: '成为技术专家', deadline: '2025-12-31', completed: false },
  { content: '建立个人技术影响力', deadline: '2025-12-31', completed: false }
])

// 对话框相关
const dialogVisible = ref(false)
const currentGoalType = ref('')
const goalForm = ref({
  content: '',
  deadline: '',
  completed: false
})

// 计算目标类型文本
const goalTypeText = computed(() => {
  const types = {
    short: '短期',
    medium: '中期',
    long: '长期'
  }
  return types[currentGoalType.value] || ''
})

// 计算进度
const calculateProgress = (goals) => {
  if (goals.length === 0) return 0
  const completed = goals.filter(goal => goal.completed).length
  return Math.round((completed / goals.length) * 100)
}

const shortTermProgress = computed(() => calculateProgress(shortTermGoals.value))
const mediumTermProgress = computed(() => calculateProgress(mediumTermGoals.value))
const longTermProgress = computed(() => calculateProgress(longTermGoals.value))

// 添加目标
const addGoal = (type) => {
  currentGoalType.value = type
  goalForm.value = {
    content: '',
    deadline: '',
    completed: false
  }
  dialogVisible.value = true
}

// 保存目标
const saveGoal = () => {
  const newGoal = { ...goalForm.value }
  switch (currentGoalType.value) {
    case 'short':
      shortTermGoals.value.push(newGoal)
      break
    case 'medium':
      mediumTermGoals.value.push(newGoal)
      break
    case 'long':
      longTermGoals.value.push(newGoal)
      break
  }
  dialogVisible.value = false
}

// 更新进度（用于复选框变化时）
const updateProgress = () => {
  // 进度会通过计算属性自动更新
}
</script>

<style scoped>
.career-goals {
  padding: 20px;
}

.goal-card {
  margin-bottom: 20px;
  height: 100%;
}

.progress-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goal-item {
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.goal-date {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  padding: 20px 0 0;
  text-align: right;
}
</style>