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
          <div class="chart-controls">
            <el-checkbox-group v-model="chartOptions" size="small" @change="updateChart">
              <el-checkbox label="volume">成交量</el-checkbox>
              <el-checkbox label="costLine">成本价线</el-checkbox>
              <el-checkbox label="maxMinLines">最值线</el-checkbox>
            </el-checkbox-group>
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
const chartOptions = ref(['costLine', 'maxMinLines']) // 默认显示成本价线和最值线，不显示成交量
const currentHistoryData = ref(null) // 存储当前的历史数据
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
      currentHistoryData.value = response.data
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

// 更新图表选项时重新渲染图表
const updateChart = () => {
  if (currentHistoryData.value) {
    updatePriceChart(currentHistoryData.value)
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

  // 计算价格最值和合适的Y轴范围
  const minPrice = Math.min(...prices)
  const maxPrice = Math.max(...prices)
  const priceRange = maxPrice - minPrice
  const padding = priceRange * 0.1 // 10% 的padding
  const yAxisMin = Math.max(0, Math.floor(minPrice - padding)) // 向下取整，确保不小于0
  const yAxisMax = Math.ceil(maxPrice + padding) // 向上取整
  const avgCost = stockInfo.value.avgCost || 0

  // 格式化X轴日期显示
  const formatXAxisDates = (dates, timeRange) => {
    if (!dates || dates.length === 0) return dates
    
    // 根据时间范围决定日期格式
    switch (timeRange) {
      case '1d':
        // 1天：只显示时间 HH:mm
        return dates.map(date => {
          if (date.includes(' ')) {
            return date.split(' ')[1] || date // 取时间部分
          }
          return date
        })
      case '1w':
        // 1周：显示月-日 时间 MM-dd HH:mm
        return dates
      case '1m':
      case '3m':
        // 1-3月：显示月-日 MM-dd
        return dates.map(date => {
          if (date.includes(' ')) {
            return date.split(' ')[0] || date // 取日期部分
          }
          return date
        })
      case '6m':
      case '1y':
      case 'ytd':
      case 'max':
        // 长期：需要显示年份，如果后端数据没有年份，则添加当前年份
        return dates.map(date => {
          if (date.includes(' ')) {
            const datePart = date.split(' ')[0]
            // 如果日期不包含年份，添加当前年份
            if (datePart.split('-').length === 2) {
              const currentYear = new Date().getFullYear()
              return `${currentYear}-${datePart}`
            }
            return datePart
          }
          // 如果没有年份，添加当前年份
          if (date.split('-').length === 2) {
            const currentYear = new Date().getFullYear()
            return `${currentYear}-${date}`
          }
          return date
        })
      default:
        return dates
    }
  }

  const formattedDates = formatXAxisDates(dates, timeRange.value)

  // 获取最新价格（数组最后一个值）
  const latestPrice = prices[prices.length - 1] || 0

  // 构建图例数据（带数值显示）
  const legendData = [`价格 ($${latestPrice.toFixed(2)})`]
  if (chartOptions.value.includes('volume')) {
    legendData.push('成交量')
  }
  if (chartOptions.value.includes('costLine') && avgCost > 0) {
    legendData.push(`成本价 ($${avgCost.toFixed(2)})`)
  }
  if (chartOptions.value.includes('maxMinLines')) {
    legendData.push(
      `最高价 ($${maxPrice.toFixed(2)})`,
      `最低价 ($${minPrice.toFixed(2)})`
    )
  }

  // 构建Y轴配置
  const yAxis = [
    {
      type: 'value',
      name: '价格',
      position: 'left',
      min: yAxisMin,
      max: yAxisMax,
      axisLabel: { 
        formatter: '${value}',
        color: '#666'
      },
      splitLine: {
        lineStyle: { color: '#f0f0f0' }
      }
    }
  ]

  // 如果显示成交量，添加第二个Y轴
  if (chartOptions.value.includes('volume')) {
    yAxis.push({
      type: 'value',
      name: '成交量',
      position: 'right',
      axisLabel: { 
        color: '#666'
      },
      splitLine: { show: false }
    })
  }

  // 构建数据系列
  const series = [
    {
      name: `价格 ($${latestPrice.toFixed(2)})`,
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
    }
  ]

  // 添加成交量系列
  if (chartOptions.value.includes('volume')) {
    const volumeData = Array.isArray(volumes) && volumes.length > 0 
      ? volumes 
      : new Array(dates.length).fill(0)
    
    series.push({
      name: '成交量',
      type: 'bar',
      yAxisIndex: 1,
      data: volumeData,
      itemStyle: {
        color: '#52c41a',
        opacity: 0.7
      },
      barWidth: '60%'
    })
  }

  // 添加成本价线
  if (chartOptions.value.includes('costLine') && avgCost > 0) {
    series.push({
      name: `成本价 ($${avgCost.toFixed(2)})`,
      type: 'line',
      data: new Array(dates.length).fill(avgCost),
      lineStyle: {
        color: '#ff7875',
        width: 2,
        type: 'dashed'
      },
      showSymbol: false,
      silent: true
    })
  }

  // 添加最值线（优化样式）
  if (chartOptions.value.includes('maxMinLines')) {
    series.push({
      name: `最高价 ($${maxPrice.toFixed(2)})`,
      type: 'line',
      data: new Array(dates.length).fill(maxPrice),
      lineStyle: {
        color: '#52c41a',  // 更明亮的绿色
        width: 2,         // 增加线宽
        type: 'dash',     // 使用dash样式替代dotted
        dashArray: [8, 4] // 自定义虚线样式：8px实线 + 4px空隙
      },
      showSymbol: false,
      silent: true,
      emphasis: {
        lineStyle: {
          width: 3        // 鼠标悬停时增加线宽
        }
      }
    })
    
    series.push({
      name: `最低价 ($${minPrice.toFixed(2)})`,
      type: 'line',
      data: new Array(dates.length).fill(minPrice),
      lineStyle: {
        color: '#f5222d',  // 更明亮的红色
        width: 2,          // 增加线宽
        type: 'dash',      // 使用dash样式替代dotted
        dashArray: [6, 3]  // 自定义虚线样式：6px实线 + 3px空隙（与最高价区分）
      },
      showSymbol: false,
      silent: true,
      emphasis: {
        lineStyle: {
          width: 3         // 鼠标悬停时增加线宽
        }
      }
    })
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      formatter: function(params) {
        // 显示原始日期和时间信息（如果有的话）
        const originalDate = dates[params[0].dataIndex] || params[0].axisValue
        let result = `${originalDate}<br/>`
        params.forEach(param => {
          if (param.seriesName.startsWith('价格')) {
            result += `${param.seriesName}: $${parseFloat(param.value).toFixed(2)}<br/>`
          } else if (param.seriesName === '成交量') {
            result += `${param.seriesName}: ${param.value}<br/>`
          } else if (param.seriesName.startsWith('成本价')) {
            result += `${param.seriesName}: $${parseFloat(param.value).toFixed(2)}<br/>`
          } else if (param.seriesName.startsWith('最高价') || param.seriesName.startsWith('最低价')) {
            result += `${param.seriesName}: $${parseFloat(param.value).toFixed(2)}<br/>`
          }
        })
        return result
      }
    },
    legend: { 
      data: legendData,
      top: 10,
      textStyle: {
        fontSize: 12,
        color: '#666'
      },
      itemGap: 20,           // 图例项之间的间距
      padding: [5, 20, 5, 20] // 图例内边距
    },
    grid: {
      left: '3%',
      right: chartOptions.value.includes('volume') ? '8%' : '4%',
      bottom: '15%',
      top: '15%',
      containLabel: true
    },
    xAxis: [{
      type: 'category',
      data: formattedDates,
      scale: true,
      boundaryGap: false,
      axisLine: { onZero: false },
      splitLine: { show: false },
      axisPointer: { label: { show: false } },
      axisLabel: {
        color: '#666',
        rotate: timeRange.value === '1y' || timeRange.value === 'max' ? 45 : 0, // 长时间范围时旋转标签
        interval: 'auto', // 自动间隔显示标签
        formatter: function(value, index) {
          // 对于长时间范围，可以进一步优化显示
          if ((timeRange.value === '1y' || timeRange.value === 'max') && formattedDates.length > 50) {
            // 长时间范围且数据点多时，每隔几个显示一个标签
            return index % Math.ceil(formattedDates.length / 20) === 0 ? value : ''
          }
          return value
        }
      }
    }],
    yAxis: yAxis,
    series: series,
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
    console.log('Chart options enabled:', chartOptions.value)
    console.log('Y-axis range:', { min: yAxisMin, max: yAxisMax })
    console.log('Price range:', { 
      latestPrice: latestPrice.toFixed(2), 
      minPrice: minPrice.toFixed(2), 
      maxPrice: maxPrice.toFixed(2), 
      avgCost: avgCost.toFixed(2) 
    })
    console.log('Time range:', timeRange.value, 'Original dates sample:', dates.slice(0, 3), 'Formatted dates sample:', formattedDates.slice(0, 3))
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

.chart-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.chart-controls .el-checkbox-group {
  margin-right: 20px;
}
</style>