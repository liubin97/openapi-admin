<template>
  <div class="test-case-management">
    <div class="header">
      <el-row :gutter="20">
        <el-col :span="16">
          <h2>测试用例管理 - {{ apiName }}</h2>
        </el-col>
        <el-col :span="8" style="text-align: right">
          <el-button type="primary" @click="handleGenerateTestCases">生成测试用例</el-button>
          <el-button type="success" @click="handleExecuteTests">执行测试</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table :data="testCases" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="name" label="用例名称" width="180" />
      <el-table-column prop="requestMethod" label="请求方法" width="100" />
      <el-table-column prop="requestPath" label="请求路径" />
      <el-table-column prop="expectedStatusCode" label="预期状态码" width="120" />
      <el-table-column prop="lastExecutionTime" label="最后执行时间" width="180">
        <template #default="{ row }">
          {{ row.lastExecutionTime ? formatDateTime(row.lastExecutionTime) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="lastExecutionStatus" label="执行状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.lastExecutionStatus)">
            {{ getStatusText(row.lastExecutionStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleViewDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="detailDialogVisible" title="测试用例详情" width="60%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用例名称">{{ currentTestCase.name }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentTestCase.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求路径">{{ currentTestCase.requestPath }}</el-descriptions-item>
        <el-descriptions-item label="请求头">
          <pre>{{ currentTestCase.requestHeaders }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="请求体">
          <pre>{{ formatJson(currentTestCase.requestBody) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="预期响应">
          <pre>{{ formatJson(currentTestCase.expectedResponse) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="实际响应">
          <pre>{{ formatJson(currentTestCase.actualResponse) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" v-if="currentTestCase.errorMessage">
          <pre style="color: red">{{ currentTestCase.errorMessage }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { get, post } from '../utils/api'

const route = useRoute()
const apiId = route.params.id
const apiName = ref('')
const testCases = ref([])
const detailDialogVisible = ref(false)
const currentTestCase = ref({})

onMounted(async () => {
  await fetchApiInfo()
  await fetchTestCases()
})

const fetchApiInfo = async () => {
  try {
    const data = await get(`/api/apis/${apiId}`)
    apiName.value = data.apiName
  } catch (error) {
    ElMessage.error('获取API信息失败')
  }
}

const fetchTestCases = async () => {
  try {
    const data = await get(`/api/test/cases/${apiId}`)
    testCases.value = data || []
  } catch (error) {
    ElMessage.error('获取测试用例失败')
  }
}

const handleGenerateTestCases = async () => {
  try {
    const { data } = await post(`/api/test/generate/${apiId}`)
    ElMessage.success(`成功生成 ${data} 个测试用例`)
    await fetchTestCases()
  } catch (error) {
    ElMessage.error('生成测试用例失败')
  }
}

const handleExecuteTests = async () => {
  try {
    await post(`/api/test/execute/${apiId}`)
    ElMessage.success('测试执行完成')
    await fetchTestCases()
  } catch (error) {
    ElMessage.error('执行测试失败')
  }
}

const handleViewDetail = (row) => {
  currentTestCase.value = row
  detailDialogVisible.value = true
}

const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString()
}

const formatJson = (json) => {
  try {
    return JSON.stringify(JSON.parse(json), null, 2)
  } catch {
    return json
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'SUCCESS': 'success',
    'FAILED': 'danger',
    'ERROR': 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'SUCCESS': '成功',
    'FAILED': '失败',
    'ERROR': '错误'
  }
  return statusMap[status] || '未知'
}
</script>

<style scoped>
.test-case-management {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  background-color: #f5f7fa;
  padding: 10px;
  margin: 0;
  border-radius: 4px;
}
</style>