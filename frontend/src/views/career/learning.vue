<template>
  <div class="career-learning">
    <el-card class="learning-card">
      <template #header>
        <div class="card-header">
          <h3>学习计划</h3>
          <el-button type="primary" @click="dialogVisible = true">添加计划</el-button>
        </div>
      </template>
      <el-table :data="learningPlans" style="width: 100%">
        <el-table-column prop="title" label="学习主题" width="180" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="deadline" label="截止日期" width="180" />
        <el-table-column prop="progress" label="进度" width="180">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="text" @click="editPlan(row)">编辑</el-button>
            <el-button type="text" class="delete-button" @click="deletePlan(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑计划对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingPlan ? '编辑学习计划' : '添加学习计划'"
      width="50%"
    >
      <el-form :model="planForm" label-width="120px">
        <el-form-item label="学习主题">
          <el-input v-model="planForm.title" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="planForm.category" placeholder="请选择类别">
            <el-option label="技术" value="technology" />
            <el-option label="管理" value="management" />
            <el-option label="语言" value="language" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="planForm.deadline"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="进度">
          <el-slider v-model="planForm.progress" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="planForm.status" placeholder="请选择状态">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePlan">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 示例数据
const learningPlans = ref([
  {
    title: 'Vue 3 深入学习',
    category: '技术',
    deadline: '2024-06-30',
    progress: 30,
    status: '进行中'
  },
  {
    title: '项目管理认证',
    category: '管理',
    deadline: '2024-08-15',
    progress: 0,
    status: '未开始'
  },
  {
    title: '英语口语提升',
    category: '语言',
    deadline: '2024-12-31',
    progress: 45,
    status: '进行中'
  }
])

const dialogVisible = ref(false)
const editingPlan = ref(null)
const planForm = ref({
  title: '',
  category: '',
  deadline: '',
  progress: 0,
  status: '未开始'
})

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    '未开始': 'info',
    '进行中': 'warning',
    '已完成': 'success'
  }
  return types[status] || 'info'
}

// 编辑计划
const editPlan = (plan) => {
  editingPlan.value = plan
  planForm.value = { ...plan }
  dialogVisible.value = true
}

// 删除计划
const deletePlan = (plan) => {
  const index = learningPlans.value.indexOf(plan)
  if (index > -1) {
    learningPlans.value.splice(index, 1)
  }
}

// 保存计划
const savePlan = () => {
  if (editingPlan.value) {
    // 更新现有计划
    Object.assign(editingPlan.value, planForm.value)
  } else {
    // 添加新计划
    learningPlans.value.push({ ...planForm.value })
  }
  dialogVisible.value = false
  editingPlan.value = null
  planForm.value = {
    title: '',
    category: '',
    deadline: '',
    progress: 0,
    status: '未开始'
  }
}
</script>

<style scoped>
.career-learning {
  padding: 20px;
}

.learning-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.delete-button {
  color: #F56C6C;
}

.dialog-footer {
  padding: 20px 0 0;
  text-align: right;
}
</style>