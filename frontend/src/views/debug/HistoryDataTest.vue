<template>
  <div class="debug-page">
    <el-card>
      <template #header>
        <h3>历史数据接口调试页面</h3>
      </template>
      
      <el-form inline>
        <el-form-item label="合约ID:">
          <el-input v-model="testConid" placeholder="请输入合约ID" style="width: 200px" />
        </el-form-item>
        <el-form-item label="时间范围:">
          <el-select v-model="testTimeRange" style="width: 120px">
            <el-option label="1天" value="1d" />
            <el-option label="1周" value="1w" />
            <el-option label="1月" value="1m" />
            <el-option label="3月" value="3m" />
            <el-option label="6月" value="6m" />
            <el-option label="1年" value="1y" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="testHistoryData" :loading="loading">
            测试接口
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <el-row :gutter="20">
        <el-col :span="12">
          <h4>原始响应数据:</h4>
          <el-input
            v-model="rawResponse"
            type="textarea"
            :rows="15"
            readonly
            placeholder="接口原始响应将显示在这里..."
          />
        </el-col>
        <el-col :span="12">
          <h4>处理后数据:</h4>
          <el-input
            v-model="processedData"
            type="textarea"
            :rows="15"
            readonly
            placeholder="处理后的图表数据将显示在这里..."
          />
        </el-col>
      </el-row>

      <el-divider />

      <div v-if="testResult">
        <h4>测试结果:</h4>
        <el-tag :type="testResult.success ? 'success' : 'danger'">
          {{ testResult.success ? '✅ 成功' : '❌ 失败' }}
        </el-tag>
        <span style="margin-left: 10px">{{ testResult.message }}</span>
        
        <div v-if="testResult.details" style="margin-top: 10px">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="数据点数量">{{ testResult.details.dataCount }}</el-descriptions-item>
            <el-descriptions-item label="日期范围">{{ testResult.details.dateRange }}</el-descriptions-item>
            <el-descriptions-item label="价格范围">{{ testResult.details.priceRange }}</el-descriptions-item>
            <el-descriptions-item label="成交量">{{ testResult.details.volumeInfo }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getHistoricalData } from '@/api/ib'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const testConid = ref('107113386') // META的合约ID作为默认值
const testTimeRange = ref('1w')
const rawResponse = ref('')
const processedData = ref('')
const testResult = ref(null)

const testHistoryData = async () => {
  if (!testConid.value.trim()) {
    ElMessage.error('请输入合约ID')
    return
  }

  loading.value = true
  testResult.value = null
  rawResponse.value = ''
  processedData.value = ''

  try {
    console.log('Testing historical data API...')
    const response = await getHistoricalData(testConid.value, testTimeRange.value)
    
    // 显示原始响应
    rawResponse.value = JSON.stringify(response, null, 2)
    
    // 处理数据
    if (response && response.data) {
      const historyData = response.data
      processedData.value = JSON.stringify(historyData, null, 2)
      
      // 验证数据格式
      const validation = validateHistoryData(historyData)
      testResult.value = validation
      
      if (validation.success) {
        ElMessage.success('接口测试成功!')
      } else {
        ElMessage.error('数据格式验证失败: ' + validation.message)
      }
    } else {
      testResult.value = {
        success: false,
        message: '响应数据格式不正确'
      }
      ElMessage.error('响应数据格式不正确')
    }
  } catch (error) {
    console.error('API test failed:', error)
    testResult.value = {
      success: false,
      message: error.message || '接口调用失败'
    }
    rawResponse.value = JSON.stringify({
      error: error.message,
      stack: error.stack
    }, null, 2)
    ElMessage.error('接口调用失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const validateHistoryData = (data) => {
  if (!data || typeof data !== 'object') {
    return { success: false, message: '数据不是有效对象' }
  }

  const { dates, prices, volumes, count } = data

  if (!Array.isArray(dates)) {
    return { success: false, message: 'dates字段不是数组' }
  }

  if (!Array.isArray(prices)) {
    return { success: false, message: 'prices字段不是数组' }
  }

  if (dates.length === 0) {
    return { success: false, message: '没有历史数据' }
  }

  if (dates.length !== prices.length) {
    return { 
      success: false, 
      message: `数据长度不匹配: dates(${dates.length}) vs prices(${prices.length})` 
    }
  }

  // 计算详细信息
  const details = {
    dataCount: dates.length,
    dateRange: dates.length > 0 ? `${dates[0]} 至 ${dates[dates.length - 1]}` : '无',
    priceRange: prices.length > 0 ? 
      `$${Math.min(...prices).toFixed(2)} - $${Math.max(...prices).toFixed(2)}` : '无',
    volumeInfo: Array.isArray(volumes) ? 
      `${volumes.length}个数据点` : '无成交量数据'
  }

  return {
    success: true,
    message: '数据格式验证通过',
    details
  }
}
</script>

<style scoped>
.debug-page {
  padding: 20px;
}

.el-textarea {
  font-family: 'Courier New', monospace;
  font-size: 12px;
}
</style> 