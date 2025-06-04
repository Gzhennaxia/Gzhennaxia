<template>
  <div class="portfolio-container">
    <router-view v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
    <div class="portfolio-dashboard" v-if="$route.name === 'Portfolio'">
      <!-- 账户概览 -->
      <el-row :gutter="20" class="mb-4">
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>总资产</span>
              </div>
            </template>
            <div class="card-value">${{ formatNumber(accountInfo.totalAssets) }}</div>
            <div class="card-change" :class="{ 'positive': accountInfo.dayChange >= 0, 'negative': accountInfo.dayChange < 0 }">
              {{ accountInfo.dayChange >= 0 ? '+' : '' }}{{ accountInfo.dayChange }}%
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>现金余额</span>
              </div>
            </template>
            <div class="card-value">${{ formatNumber(accountInfo.cashBalance) }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>持仓市值</span>
              </div>
            </template>
            <div class="card-value">${{ formatNumber(accountInfo.positionValue) }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>未实现盈亏</span>
              </div>
            </template>
            <div class="card-value" :class="{ 'positive': accountInfo.unrealizedPnL >= 0, 'negative': accountInfo.unrealizedPnL < 0 }">
              ${{ formatNumber(accountInfo.unrealizedPnL) }}
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 资产配置和收益分析 -->
      <el-row :gutter="20" class="mb-4">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>资产配置</span>
                <el-tooltip content="资产类别分布" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <div class="chart-container">
              <div ref="assetAllocationChart" style="height: 300px"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>收益走势</span>
                <el-tooltip content="历史收益率变化" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <div class="chart-container">
              <div ref="performanceChart" style="height: 300px"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 风险指标 -->
      <el-row :gutter="20" class="mb-4">
        <el-col :span="8" v-for="metric in riskMetrics" :key="metric.name">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>{{ metric.name }}</span>
                <el-tooltip :content="metric.description" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <div class="metric-value" :class="metric.status">
              {{ metric.value }}
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 持仓列表 -->
      <el-card shadow="hover" class="mb-4">
        <template #header>
          <div class="card-header">
            <span>持仓明细</span>
            <el-button type="primary" size="small" @click="refreshData">刷新</el-button>
          </div>
        </template>
        <el-table :data="positions" style="width: 100%" v-loading="loading">
          <el-table-column prop="conid" label="合约ID" width="100" sortable />
          <el-table-column prop="symbol" label="代码" width="120" sortable />
          <el-table-column prop="contractDesc" label="名称" width="200" sortable>
            <template #default="scope">
              <el-link type="primary" @click="$router.push(`/finance/portfolio/stock/${scope.row.conid}`)">
                {{ scope.row.contractDesc }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="position" label="数量" width="100" align="right" sortable />
          <el-table-column prop="avgPrice" label="均价" width="120" align="right" sortable>
            <template #default="scope">
              ${{ formatNumber(scope.row.avgPrice) }}
            </template>
          </el-table-column>
          <el-table-column prop="mktPrice" label="市价" width="120" align="right" sortable>
            <template #default="scope">
              ${{ formatNumber(scope.row.mktPrice) }}
            </template>
          </el-table-column>
          <el-table-column prop="avgCost" label="成本价" width="120" align="right" sortable>
            <template #default="scope">
              ${{ formatNumber(scope.row.avgCost) }}
            </template>
          </el-table-column>
          <el-table-column prop="mktValue" label="市值" width="120" align="right" sortable>
            <template #default="scope">
              ${{ formatNumber(scope.row.mktValue) }}
            </template>
          </el-table-column>
          <el-table-column prop="realizedPnl" label="已实现盈亏" width="120" align="right" sortable>
            <template #default="scope">
              <span :class="{ 'positive': scope.row.realizedPnl >= 0, 'negative': scope.row.realizedPnl < 0 }">
                ${{ formatNumber(scope.row.realizedPnl) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="unrealizedPnl" label="未实现盈亏" width="120" align="right" sortable>
            <template #default="scope">
              <span :class="{ 'positive': scope.row.unrealizedPnl >= 0, 'negative': scope.row.unrealizedPnl < 0 }">
                ${{ formatNumber(scope.row.unrealizedPnl) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="returnRate" label="收益率" align="right" sortable>
            <template #default="scope">
              <span :class="{ 'positive': scope.row.returnRate >= 0, 'negative': scope.row.returnRate < 0 }">
                {{ scope.row.returnRate >= 0 ? '+' : '' }}{{ formatNumber(scope.row.returnRate) }}%
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getAccountInfo, getPortfolio, getPositions, refreshPositions, getAssetAllocation, getPerformanceAnalysis, getRiskMetrics } from '@/api/ib'
import { ElMessage } from 'element-plus'

// 数据状态
const loading = ref(false)
const accountInfo = ref({
  totalAssets: 0,
  dayChange: 0,
  cashBalance: 0,
  positionValue: 0,
  unrealizedPnL: 0
})
const positions = ref([])
const riskMetrics = ref([])

// 图表实例
let assetAllocationChart = null
let performanceChart = null

// 自动刷新定时器
let refreshTimer = null

// 格式化数字
const formatNumber = (num) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(num)
}

// 加载账户信息
const loadAccountInfo = async () => {
  try {
    const accountRes = await getAccountInfo()
    accountInfo.value = accountRes.data
  } catch (error) {
    console.error('获取账户信息失败:', error)
    ElMessage.error('获取账户信息失败')
  }
}

// 加载持仓信息
const loadPositions = async () => {
  try {
    loading.value = true
    const positionsRes = await getPositions()
    positions.value = positionsRes.data
  } catch (error) {
    console.error('获取持仓信息失败:', error)
    ElMessage.error('获取持仓信息失败')
  } finally {
    loading.value = false
  }
}

// 加载资产配置数据
const loadAssetAllocation = async () => {
  try {
    const allocationRes = await getAssetAllocation()
    updateAssetAllocationChart(allocationRes.data)
  } catch (error) {
    console.error('获取资产配置数据失败:', error)
    ElMessage.error('获取资产配置数据失败')
  }
}

// 加载收益分析数据
const loadPerformanceAnalysis = async () => {
  try {
    const performanceRes = await getPerformanceAnalysis()
    updatePerformanceChart(performanceRes.data)
  } catch (error) {
    console.error('获取收益分析数据失败:', error)
    ElMessage.error('获取收益分析数据失败')
  }
}

// 加载风险指标
const loadRiskMetrics = async () => {
  try {
    const riskRes = await getRiskMetrics()
    riskMetrics.value = riskRes.data
  } catch (error) {
    console.error('获取风险指标失败:', error)
    ElMessage.error('获取风险指标失败')
  }
}

// 加载所有数据
const loadAllData = () => {
  Promise.all([
    loadAccountInfo(),
    loadPositions(),
    loadAssetAllocation(),
    loadPerformanceAnalysis(),
    loadRiskMetrics()
  ])
}

const reloadPositions = async () => {
    loading.value = true
    try {
      // 获取持仓信息
      const positionsRes = await refreshPositions()
      positions.value = positionsRes.data
      ElMessage.success('数据已更新')
    } catch (error) {
      console.error('更新数据失败:', error)
      ElMessage.error('更新数据失败')
    } finally {
      loading.value = false
    }
}

// 手动刷新数据
const refreshData = () => {
  reloadPositions()
}

// 更新资产配置图表
const updateAssetAllocationChart = (data) => {
  if (!assetAllocationChart) return

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [{
      type: 'pie',
      radius: '70%',
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }

  assetAllocationChart.setOption(option)
}

// 更新收益走势图表
const updatePerformanceChart = (data) => {
  if (!performanceChart) return

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      data: data.returns,
      type: 'line',
      smooth: true
    }]
  }

  performanceChart.setOption(option)
}

// 初始化
onMounted(() => {
  // 初始化图表
  // assetAllocationChart = echarts.init(document.querySelector('#assetAllocationChart'))
  // performanceChart = echarts.init(document.querySelector('#performanceChart'))

  // 加载数据
  loadAllData()

  // 设置自动刷新（每5分钟）
  // refreshTimer = setInterval(loadAllData, 5 * 60 * 1000)

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    assetAllocationChart?.resize()
    performanceChart?.resize()
  })
})

// 清理
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  assetAllocationChart?.dispose()
  performanceChart?.dispose()
})
</script>

<style scoped>
.portfolio-container {
  width: 100%;
  height: 100%;
}

.portfolio-dashboard {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.card-change {
  font-size: 14px;
}

.positive {
  color: #67C23A;
}

.negative {
  color: #F56C6C;
}

.metric-value {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
}

.metric-value.warning {
  color: #E6A23C;
}

.metric-value.danger {
  color: #F56C6C;
}

.chart-container {
  position: relative;
  width: 100%;
  height: 300px;
}
</style>