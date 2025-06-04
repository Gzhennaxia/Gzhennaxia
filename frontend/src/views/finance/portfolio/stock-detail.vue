<template>
  <div class="stock-detail">
    <!-- 基本信息 -->
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="primary" size="small" @click="refreshData">刷新</el-button>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="合约ID">{{ stockInfo.conid }}</el-descriptions-item>
        <el-descriptions-item label="代码">{{ stockInfo.symbol }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ stockInfo.contractDesc }}</el-descriptions-item>
        <el-descriptions-item label="持仓数量">{{ stockInfo.position }}</el-descriptions-item>
        <el-descriptions-item label="成本价">${{ formatNumber(stockInfo.avgCost) }}</el-descriptions-item>
        <el-descriptions-item label="市价">${{ formatNumber(stockInfo.mktPrice) }}</el-descriptions-item>
        <el-descriptions-item label="市值">${{ formatNumber(stockInfo.mktValue) }}</el-descriptions-item>
        <el-descriptions-item label="未实现盈亏">
          <span :class="{ 'positive': stockInfo.unrealizedPnl >= 0, 'negative': stockInfo.unrealizedPnl < 0 }">
            ${{ formatNumber(stockInfo.unrealizedPnl) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="收益率">
          <span :class="{ 'positive': stockInfo.returnRate >= 0, 'negative': stockInfo.returnRate < 0 }">
            {{ stockInfo.returnRate >= 0 ? '+' : '' }}{{ formatNumber(stockInfo.returnRate) }}%
          </span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 历史数据图表 -->
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>历史数据</span>
          <el-radio-group v-model="timeRange" size="small" @change="loadHistoricalData">
            <el-radio-button label="1d">1天</el-radio-button>
            <el-radio-button label="1w">1周</el-radio-button>
            <el-radio-button label="1m">1月</el-radio-button>
            <el-radio-button label="3m">3月</el-radio-button>
            <el-radio-button label="6m">6月</el-radio-button>
            <el-radio-button label="1y">1年</el-radio-button>
            <el-radio-button label="ytd">今年</el-radio-button>
            <el-radio-button label="max">全部</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div class="chart-container" v-loading="loading">
        <div ref="priceChart" style="height: 400px"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import { getStockInfo, getHistoricalData } from '@/api/ib'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const timeRange = ref('1m')
const stockInfo = ref({
  conid: '',
  symbol: '',
  contractDesc: '',
  position: 0,
  avgCost: 0,
  mktPrice: 0,
  mktValue: 0,
  unrealizedPnl: 0,
  returnRate: 0
})

// 图表实例
let priceChart = null

// 格式化数字
const formatNumber = (num) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(num)
}

// 加载股票信息
const loadStockInfo = async () => {
  try {
    loading.value = true
    const { data } = await getStockInfo(route.params.id)
    stockInfo.value = data
  } catch (error) {
    ElMessage.error('加载股票信息失败')
    console.error('Failed to load stock info:', error)
  } finally {
    loading.value = false
  }
}

// 加载历史数据
const loadHistoricalData = async () => {
  try {
    loading.value = true
    const { data } = await getHistoricalData(route.params.id, timeRange.value)
    updatePriceChart(data)
  } catch (error) {
    ElMessage.error('加载历史数据失败')
    console.error('Failed to load historical data:', error)
  } finally {
    loading.value = false
  }
}

// 更新价格图表
const updatePriceChart = (data) => {
  if (!priceChart) return

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: { data: ['价格', '成交量'] },
    grid: {
      left: '3%',
      right: '3%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: [{
      type: 'category',
      data: data.dates,
      scale: true,
      boundaryGap: false,
      axisLine: { onZero: false },
      splitLine: { show: false },
      axisPointer: { label: { show: false } }
    }],
    yAxis: [
      {
        type: 'value',
        name: '价格',
        position: 'left',
        axisLabel: { formatter: '${value}' }
      },
      {
        type: 'value',
        name: '成交量',
        position: 'right',
        offset: 80
      }
    ],
    series: [
      {
        name: '价格',
        type: 'line',
        data: data.prices,
        smooth: true,
        showSymbol: false,
        lineStyle: { width: 1 }
      },
      {
        name: '成交量',
        type: 'bar',
        yAxisIndex: 1,
        data: data.volumes
      }
    ],
    dataZoom: [
      {
        type: 'inside',
        start: 0,
        end: 100
      },
      {
        show: true,
        type: 'slider',
        bottom: '5%',
        start: 0,
        end: 100
      }
    ]
  }

  priceChart.setOption(option)
}

// 刷新数据
const refreshData = async () => {
  await loadStockInfo()
  await loadHistoricalData()
}

// 初始化图表
const initChart = () => {
  if (priceChart) return
  const chartDom = document.querySelector('#priceChart')
  priceChart = echarts.init(chartDom)
  window.addEventListener('resize', () => priceChart?.resize())
}

// 组件挂载时
onMounted(async () => {
  initChart()
  await refreshData()
})

// 组件卸载时
onUnmounted(() => {
  if (priceChart) {
    priceChart.dispose()
    priceChart = null
  }
  window.removeEventListener('resize', () => priceChart?.resize())
})
</script>

<style scoped>
.stock-detail {
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

.positive {
  color: #67c23a;
}

.negative {
  color: #f56c6c;
}

.chart-container {
  margin-top: 20px;
}
</style>