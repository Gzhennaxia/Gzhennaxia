<template>
  <div class="career-skills">
    <el-card class="skills-card">
      <template #header>
        <div class="card-header">
          <h3>技能概览</h3>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="(skill, index) in skills" :key="index">
          <el-card class="skill-item" shadow="hover">
            <h4>{{ skill.name }}</h4>
            <el-progress
              :percentage="skill.level"
              :color="getProgressColor(skill.level)"
            />
            <p class="skill-description">{{ skill.description }}</p>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 示例数据
const skills = ref([
  {
    name: '前端开发',
    level: 85,
    description: 'Vue.js, React, JavaScript, HTML5, CSS3'
  },
  {
    name: '后端开发',
    level: 75,
    description: 'Java, Spring Boot, MySQL, Redis'
  },
  {
    name: '系统设计',
    level: 70,
    description: '架构设计, 数据建模, API设计'
  }
])

// 根据技能等级返回不同的进度条颜色
const getProgressColor = (level) => {
  if (level >= 80) return '#67C23A'
  if (level >= 60) return '#E6A23C'
  return '#F56C6C'
}
</script>

<style scoped>
.career-skills {
  padding: 20px;
}

.skills-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skill-item {
  margin-bottom: 20px;
}

.skill-description {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}
</style>