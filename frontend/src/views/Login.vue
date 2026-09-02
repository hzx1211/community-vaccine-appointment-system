<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-wrapper">
      <!-- 左侧介绍 -->
      <div class="login-intro">
        <div class="intro-content">
          <div class="logo">
            <el-icon size="48"><FirstAidKit /></el-icon>
          </div>
          <h1>社区疫苗预约管理系统</h1>
          <p class="slogan">便捷预约 · 安全接种 · 健康生活</p>
          <div class="features">
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>在线预约疫苗接种</span>
            </div>
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>查看社区健康资讯</span>
            </div>
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>管理个人接种记录</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="login-box">
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p>请选择您的身份进行登录</p>
        </div>
        
        <!-- 登录类型切换 -->
        <div class="login-tabs">
          <div 
            class="tab-item" 
            :class="{ active: loginType === 'user' }" 
            @click="loginType = 'user'"
          >
            <div class="tab-icon">
              <el-icon><User /></el-icon>
            </div>
            <span>用户登录</span>
          </div>
          <div 
            class="tab-item" 
            :class="{ active: loginType === 'admin' }" 
            @click="loginType = 'admin'"
          >
            <div class="tab-icon">
              <el-icon><Setting /></el-icon>
            </div>
            <span>管理员登录</span>
          </div>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="0" class="login-form">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名" 
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码" 
              size="large" 
              show-password
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              class="login-btn"
              :loading="loading" 
              @click="handleLogin"
            >
              <el-icon v-if="!loading"><Right /></el-icon>
              {{ loading ? '登录中...' : '立即登录' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer" v-if="loginType === 'user'">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
        <div class="login-footer tips" v-else>
          <el-icon><InfoFilled /></el-icon>
          <span>管理员账号请联系系统管理员获取</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { FirstAidKit, CircleCheck, User, Setting, Lock, Right, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const loginType = ref('user')

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await userStore.login(form)
    
    if (loginType.value === 'user') {
      if (data.role !== 'user') {
        ElMessage.error('请使用管理员登录入口')
        userStore.logout()
        return
      }
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      if (data.role === 'user') {
        ElMessage.error('该账号不是管理员，请使用用户登录')
        userStore.logout()
        return
      }
      ElMessage.success('登录成功')
      router.push('/admin')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

// 背景装饰
.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  
  .circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 6s ease-in-out infinite;
  }
  
  .circle-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    left: -100px;
    animation-delay: 0s;
  }
  
  .circle-2 {
    width: 300px;
    height: 300px;
    bottom: -50px;
    right: -50px;
    animation-delay: 2s;
  }
  
  .circle-3 {
    width: 200px;
    height: 200px;
    top: 50%;
    left: 50%;
    animation-delay: 4s;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

.login-wrapper {
  display: flex;
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  max-width: 900px;
  width: 100%;
  position: relative;
  z-index: 10;
}

// 左侧介绍
.login-intro {
  width: 400px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.95) 0%, rgba(118, 75, 162, 0.95) 100%);
  padding: 50px 40px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    top: -100px;
    right: -100px;
  }
  
  &::after {
    content: '';
    position: absolute;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.08);
    border-radius: 50%;
    bottom: -50px;
    left: -50px;
  }
}

.intro-content {
  position: relative;
  z-index: 10;
  color: #fff;
  
  .logo {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 30px;
    backdrop-filter: blur(10px);
  }
  
  h1 {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 12px;
  }
  
  .slogan {
    font-size: 15px;
    opacity: 0.9;
    margin-bottom: 40px;
    letter-spacing: 2px;
  }
}

.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.9;
  
  .el-icon {
    font-size: 20px;
    color: #85ce61;
  }
}

// 右侧登录表单
.login-box {
  flex: 1;
  padding: 50px 45px;
  display: flex;
  flex-direction: column;
}

.login-header {
  margin-bottom: 30px;
  
  h2 {
    font-size: 26px;
    color: var(--text-primary);
    font-weight: 700;
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-secondary);
    font-size: 14px;
  }
}

.login-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
  
  .tab-item {
    flex: 1;
    padding: 16px;
    border: 2px solid var(--border-light);
    border-radius: 12px;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    transition: var(--transition);
    
    .tab-icon {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      background: var(--bg-color);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      color: var(--text-secondary);
      transition: var(--transition);
    }
    
    span {
      font-size: 14px;
      color: var(--text-regular);
      font-weight: 500;
    }
    
    &:hover {
      border-color: var(--primary-light);
      
      .tab-icon {
        color: var(--primary-color);
      }
    }
    
    &.active {
      border-color: var(--primary-color);
      background: linear-gradient(135deg, rgba(64, 158, 255, 0.05) 0%, rgba(102, 126, 234, 0.05) 100%);
      
      .tab-icon {
        background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
        color: #fff;
      }
      
      span {
        color: var(--primary-color);
      }
    }
  }
}

.login-form {
  :deep(.el-input__wrapper) {
    padding: 4px 15px;
    border-radius: 10px;
    box-shadow: 0 0 0 1px var(--border-color) inset;
    
    &:hover, &.is-focus {
      box-shadow: 0 0 0 1px var(--primary-color) inset;
    }
  }
  
  :deep(.el-input__inner) {
    height: 44px;
  }
  
  :deep(.el-input__prefix) {
    font-size: 18px;
    color: var(--text-secondary);
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  &:hover {
    background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  }
}

.login-footer {
  text-align: center;
  margin-top: auto;
  padding-top: 20px;
  color: var(--text-secondary);
  font-size: 14px;
  
  a {
    color: var(--primary-color);
    font-weight: 500;
    margin-left: 4px;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  &.tips {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    color: var(--text-placeholder);
    font-size: 13px;
  }
}

// 响应式
@media (max-width: 900px) {
  .login-intro {
    display: none;
  }
  
  .login-wrapper {
    max-width: 440px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }
  
  .login-box {
    padding: 30px 24px;
  }
  
  .login-header h2 {
    font-size: 22px;
  }
  
  .login-tabs .tab-item {
    padding: 12px;
    
    .tab-icon {
      width: 38px;
      height: 38px;
      font-size: 18px;
    }
    
    span {
      font-size: 13px;
    }
  }
}
</style>
