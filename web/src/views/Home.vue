<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>API总数</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.apiCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>版本总数</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.versionCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>调用总次数</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.callCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-activity" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :timestamp="activity.time"
          :type="activity.type">
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const stats = ref({
  apiCount: 0,
  versionCount: 0,
  callCount: 0
})

const recentActivities = ref([
  {
    id: 1,
    content: '新增API：用户认证接口',
    time: '2024-01-20 10:00',
    type: 'primary'
  },
  {
    id: 2,
    content: '更新版本：支付接口v2.0',
    time: '2024-01-19 15:30',
    type: 'success'
  },
  {
    id: 3,
    content: '删除API：废弃的数据接口',
    time: '2024-01-18 09:15',
    type: 'danger'
  }
])

// TODO: 从后端获取统计数据和活动列表
const fetchStats = async () => {
  try {
    const response = await fetch('/api/dashboard/stats', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    if (response.ok) {
      const data = await response.json()
      stats.value = data
    }
  } catch (error) {
    console.error('获取统计数据失败：', error)
  }
}

const fetchActivities = async () => {
  try {
    const response = await fetch('/api/dashboard/activities', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    if (response.ok) {
      const data = await response.json()
      recentActivities.value = data
    }
  } catch (error) {
    console.error('获取活动列表失败：', error)
  }
}
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.stat-card {
  .stat-value {
    font-size: 36px;
    font-weight: bold;
    text-align: center;
    color: #409EFF;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>