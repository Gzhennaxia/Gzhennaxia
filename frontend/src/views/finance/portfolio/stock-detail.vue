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
        <div ref="priceChartRef" style="height: 400px"></div>
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
const priceChartRef = ref(null)
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
    const response = await getHistoricalData(route.params.id, timeRange.value)
    console.log('Historical data response:', response) // 添加调试日志
    
    // 正确获取历史数据：response.data 才是真正的历史数据
    if (response && response.data) {
      updatePriceChart(response.data)
    } else {
      ElMessage.error('返回的历史数据格式不正确')
      console.error('Invalid historical data format:', response)
    }
  } catch (error) {
    ElMessage.error('加载历史数据失败: ' + (error.message || '未知错误'))
    console.error('Failed to load historical data:', error)
  } finally {
    loading.value = false
  }
}

// 更新价格图表
const updatePriceChart = (data) => {
  if (!priceChart) {
    console.error('Chart instance not initialized')
    return
  }

  // 验证数据格式
  if (!data || typeof data !== 'object') {
    console.error('Invalid chart data:', data)
    ElMessage.error('图表数据格式不正确')
    return
  }

  console.log('Chart data received:', data) // 调试日志

  // 检查必需的数据字段
  const { dates, prices, volumes, count } = data
  
  if (!Array.isArray(dates) || !Array.isArray(prices)) {
    console.error('Missing required data fields:', { dates, prices, volumes })
    ElMessage.error('历史数据不完整，缺少必要字段')
    return
  }

  if (dates.length === 0 || prices.length === 0) {
    console.warn('Empty historical data')
    ElMessage.warning('暂无历史数据')
    
    // 显示空数据图表
    const emptyOption = {
      title: {
        text: '暂无数据',
        left: 'center',
        top: 'center',
        textStyle: { color: '#999' }
      },
      xAxis: { show: false },
      yAxis: { show: false }
    }
    priceChart.setOption(emptyOption)
    return
  }

  // 确保成交量数据存在，如果不存在则使用默认值
  const volumeData = Array.isArray(volumes) && volumes.length > 0 
    ? volumes 
    : new Array(dates.length).fill(0)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      formatter: function(params) {
        let result = `${params[0].axisValue}<br/>`
        params.forEach(param => {
          if (param.seriesName === '价格') {
            result += `${param.seriesName}: $${param.value}<br/>`
          } else {
            result += `${param.seriesName}: ${param.value}<br/>`
          }
        })
        return result
      }
    },
    legend: { 
      data: ['价格', '成交量'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '8%',
      bottom: '15%',
      top: '15%',
      containLabel: true
    },
    xAxis: [{
      type: 'category',
      data: dates,
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
        axisLabel: { 
          formatter: '${value}',
          color: '#666'
        },
        splitLine: {
          lineStyle: { color: '#f0f0f0' }
        }
      },
      {
        type: 'value',
        name: '成交量',
        position: 'right',
        axisLabel: { 
          color: '#666'
        },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '价格',
        type: 'line',
        data: prices,
        smooth: true,
        showSymbol: false,
        lineStyle: { 
          width: 2,
          color: '#1890ff'
        },
        areaStyle: {
          opacity: 0.1,
          color: '#1890ff'
        }
      },
      {
        name: '成交量',
        type: 'bar',
        yAxisIndex: 1,
        data: volumeData,
        itemStyle: {
          color: '#52c41a',
          opacity: 0.7
        },
        barWidth: '60%'
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
        end: 100,
        height: 20
      }
    ]
  }

  try {
    priceChart.setOption(option, true) // true 表示不合并，完全替换
    console.log('Chart updated successfully with', dates.length, 'data points')
  } catch (error) {
    console.error('Failed to update chart:', error)
    ElMessage.error('图表更新失败')
  }
}

// 刷新数据
const refreshData = async () => {
  await loadStockInfo()
  await loadHistoricalData()
}

// 初始化图表
const initChart = () => {
  if (priceChart) {
    console.log('Chart already initialized')
    return
  }
  
  if (!priceChartRef.value) {
    console.error('Chart DOM element not found')
    return
  }
  
  try {
    priceChart = echarts.init(priceChartRef.value)
    console.log('Chart initialized successfully')
    
    // 添加窗口大小变化监听
    const handleResize = () => {
      if (priceChart) {
        priceChart.resize()
      }
    }
    window.addEventListener('resize', handleResize)
    
    // 保存清理函数的引用
    priceChart._resizeHandler = handleResize
  } catch (error) {
    console.error('Failed to initialize chart:', error)
    ElMessage.error('图表初始化失败')
  }
}

// 组件挂载时
onMounted(async () => {
  initChart()
  await refreshData()
})

// 组件卸载时
onUnmounted(() => {
  if (priceChart) {
    // 移除窗口大小变化监听器
    if (priceChart._resizeHandler) {
      window.removeEventListener('resize', priceChart._resizeHandler)
    }
    
    // 销毁图表实例
    priceChart.dispose()
    priceChart = null
    console.log('Chart disposed successfully')
  }
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