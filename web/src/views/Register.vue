<template>
  <div class="auth-container register-container">
    <!-- 装饰元素 -->
    <div class="decoration-circle decoration-circle-1"></div>
    <div class="decoration-circle decoration-circle-2"></div>
    <div class="decoration-circle decoration-circle-3"></div>
    
    <!-- 系统标题 -->
    <div class="system-title">OpenAPI 管理系统</div>
    <div class="system-subtitle">高效、安全的API管理平台</div>
    
    <el-card class="auth-card register-card">
      <div class="auth-header">
        <h2><el-icon class="api-icon"><Key /></el-icon>用户注册</h2>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="auth-form">
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名" 
            :prefix-icon="User" 
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码" 
            :prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请确认密码" 
            :prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            class="auth-button" 
            @click="handleRegister"
            :loading="loading"
          >注册</el-button>
        </el-form-item>
        <div class="auth-link">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ validator: validatePass, trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await fetch('/api/auth/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: registerForm.username,
            password: registerForm.password
          })
        })
        
        const data = await response.json()
        if (response.ok) {
          ElMessage.success('注册成功')
          router.push('/login')
        } else {
          ElMessage.error(data.message || '注册失败')
        }
      } catch (error) {
        ElMessage.error('注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 注册页面特定样式，全局样式在global.css中 */
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>