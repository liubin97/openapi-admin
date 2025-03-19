<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        @select="handleSelect">
        <el-menu-item index="/">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/api">
          <el-icon><Document /></el-icon>
          <span>API管理</span>
        </el-menu-item>
        <el-menu-item index="/version">
          <el-icon><Files /></el-icon>
          <span>版本管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="tabs-container">
          <el-tabs 
            v-model="activeTab" 
            type="card" 
            closable 
            @tab-remove="removeTab"
            @tab-click="clickTab">
            <el-tab-pane
              v-for="item in tabs"
              :key="item.path"
              :label="item.title"
              :name="item.path">
            </el-tab-pane>
          </el-tabs>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { House, Document, Files, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const username = ref('Admin') // TODO: 从用户状态获取用户名

// 页签相关数据
const tabs = ref([])
const activeTab = ref('')

// 菜单激活状态
const activeMenu = computed(() => {
  return route.path
})

// 路由路径与标题的映射
const routeTitles = {
  '/': '首页',
  '/api': 'API管理',
  '/version': '版本管理'
}

// 初始化页签
onMounted(() => {
  addTab(route.path)
})

// 监听路由变化
watch(() => route.path, (newPath) => {
  activeTab.value = newPath
  addTab(newPath)
})

// 添加页签
const addTab = (path) => {
  // 如果页签不存在，则添加
  if (!tabs.value.some(tab => tab.path === path)) {
    tabs.value.push({
      title: routeTitles[path] || '未知页面',
      path: path
    })
  }
  activeTab.value = path
}

// 移除页签
const removeTab = (targetPath) => {
  // 不允许关闭所有页签
  if (tabs.value.length <= 1) {
    return
  }
  
  const tabIndex = tabs.value.findIndex(tab => tab.path === targetPath)
  
  // 如果关闭的是当前激活的页签，则需要激活其他页签
  if (activeTab.value === targetPath) {
    // 优先激活右侧页签，如果没有则激活左侧页签
    const nextTab = tabs.value[tabIndex + 1] || tabs.value[tabIndex - 1]
    if (nextTab) {
      activeTab.value = nextTab.path
      router.push(nextTab.path)
    }
  }
  
  // 移除页签
  tabs.value.splice(tabIndex, 1)
}

// 点击页签
const clickTab = (tab) => {
  router.push(tab.props.name)
}

// 选择菜单
const handleSelect = (index) => {
  router.push(index)
}

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    ElMessage.success('退出成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-header {
  background-color: #fff;
  color: #333;
  padding: 0;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.el-aside {
  background-color: #304156;
}

.el-menu {
  border-right: none;
}

.tabs-container {
  flex: 1;
  padding-left: 10px;
}

.header-right {
  padding-right: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 100%;
}

.el-dropdown-link {
  color: #333;
  display: flex;
  align-items: center;
}

:deep(.el-tabs__header) {
  margin-bottom: 0;
}

:deep(.el-tabs__nav) {
  border: none;
}

:deep(.el-tabs__item) {
  height: 40px;
  line-height: 40px;
}

:deep(.el-tabs__item.is-active) {
  background-color: #f0f2f5;
}
</style>