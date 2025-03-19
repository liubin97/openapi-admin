<template>
  <div class="auth-container login-container">
    <!-- 装饰元素 -->
    <div class="decoration-circle decoration-circle-1"></div>
    <div class="decoration-circle decoration-circle-2"></div>
    <div class="decoration-circle decoration-circle-3"></div>
    
    <!-- 系统标题 -->
    <div class="system-title">OpenAPI 管理系统</div>
    <div class="system-subtitle">高效、安全的API管理平台</div>
    
    <el-card class="auth-card login-card">
      <div class="auth-header">
        <h2><el-icon class="api-icon"><Connection /></el-icon>用户登录</h2>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="auth-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            :prefix-icon="User" 
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            :prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            class="auth-button" 
            @click="handleLogin"
            :loading="loading"
          >登录</el-button>
        </el-form-item>
        <div class="auth-link">
          <router-link to="/register">还没有账号？立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Connection } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // TODO: 调用登录API
        const response = await fetch('/api/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(loginForm)
        })
        
        const data = await response.json()
        if (response.ok) {
          localStorage.setItem('token', data.token)
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error(data.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 登录页面特定样式，全局样式在global.css中 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>