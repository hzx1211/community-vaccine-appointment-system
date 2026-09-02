<template>
  <div class="front-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <div class="logo-icon">
            <el-icon size="24"><FirstAidKit /></el-icon>
          </div>
          <span class="logo-text">社区疫苗预约系统</span>
        </div>
        
        <!-- 移动端菜单按钮 -->
        <el-icon class="mobile-menu-btn" @click="mobileMenuVisible = true">
          <Menu />
        </el-icon>
        
        <!-- PC端导航 -->
        <nav class="nav desktop-nav">
          <router-link to="/" class="nav-item">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </router-link>
          <router-link to="/vaccine" class="nav-item">
            <el-icon><FirstAidKit /></el-icon>
            <span>疫苗接种</span>
          </router-link>
          <router-link to="/community" class="nav-item">
            <el-icon><OfficeBuilding /></el-icon>
            <span>社区风采</span>
          </router-link>
          <router-link to="/news" class="nav-item">
            <el-icon><Document /></el-icon>
            <span>社区资讯</span>
          </router-link>
          <router-link to="/my/appointment" class="nav-item" v-if="userStore.isLoggedIn">
            <el-icon><Calendar /></el-icon>
            <span>我的预约</span>
          </router-link>
        </nav>
        
        <div class="user-area desktop-nav">
          <template v-if="userStore.isLoggedIn">
            <div class="balance-info">
              <el-icon><Wallet /></el-icon>
              <span>¥{{ userStore.userInfo.balance || '0.00' }}</span>
            </div>
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="user-info">
                <el-avatar :size="36" :src="userStore.userInfo.avatar">
                  {{ userStore.userInfo.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
                <el-icon class="arrow"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="appointment">
                    <el-icon><Calendar /></el-icon>我的预约
                  </el-dropdown-item>
                  <el-dropdown-item command="collection">
                    <el-icon><Star /></el-icon>我的关注
                  </el-dropdown-item>
                  <el-dropdown-item command="recharge">
                    <el-icon><Wallet /></el-icon>我的充值
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" round @click="router.push('/login')">
              <el-icon><User /></el-icon>登录
            </el-button>
            <el-button round @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 移动端抽屉菜单 -->
    <el-drawer v-model="mobileMenuVisible" direction="ltr" size="280px" :show-close="false">
      <template #header>
        <div class="drawer-header">
          <div class="logo-icon">
            <el-icon size="20"><FirstAidKit /></el-icon>
          </div>
          <span>疫苗预约系统</span>
        </div>
      </template>
      <div class="mobile-menu">
        <div class="mobile-user" v-if="userStore.isLoggedIn">
          <el-avatar :size="50" :src="userStore.userInfo.avatar">
            {{ userStore.userInfo.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <div class="user-detail">
            <span class="name">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
            <span class="balance">余额: ¥{{ userStore.userInfo.balance || '0.00' }}</span>
          </div>
        </div>
        <div class="mobile-nav">
          <router-link to="/" class="mobile-nav-item" @click="mobileMenuVisible = false">
            <el-icon><HomeFilled /></el-icon>首页
          </router-link>
          <router-link to="/vaccine" class="mobile-nav-item" @click="mobileMenuVisible = false">
            <el-icon><FirstAidKit /></el-icon>疫苗接种
          </router-link>
          <router-link to="/community" class="mobile-nav-item" @click="mobileMenuVisible = false">
            <el-icon><OfficeBuilding /></el-icon>社区风采
          </router-link>
          <router-link to="/news" class="mobile-nav-item" @click="mobileMenuVisible = false">
            <el-icon><Document /></el-icon>社区资讯
          </router-link>
          <template v-if="userStore.isLoggedIn">
            <div class="nav-divider"></div>
            <router-link to="/my/appointment" class="mobile-nav-item" @click="mobileMenuVisible = false">
              <el-icon><Calendar /></el-icon>我的预约
            </router-link>
            <router-link to="/my/collection" class="mobile-nav-item" @click="mobileMenuVisible = false">
              <el-icon><Star /></el-icon>我的关注
            </router-link>
            <router-link to="/my/recharge" class="mobile-nav-item" @click="mobileMenuVisible = false">
              <el-icon><Wallet /></el-icon>我的充值
            </router-link>
            <router-link to="/my/profile" class="mobile-nav-item" @click="mobileMenuVisible = false">
              <el-icon><User /></el-icon>个人信息
            </router-link>
            <div class="nav-divider"></div>
            <div class="mobile-nav-item logout" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </div>
          </template>
          <template v-else>
            <div class="mobile-auth">
              <el-button type="primary" round size="large" @click="goLogin">登录</el-button>
              <el-button round size="large" @click="goRegister">注册</el-button>
            </div>
          </template>
        </div>
      </div>
    </el-drawer>

    <!-- 主内容区 -->
    <main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-content">
        <!-- 左侧信息 -->
        <div class="footer-brand">
          <div class="footer-logo">
            <div class="logo-icon">
              <el-icon size="24"><FirstAidKit /></el-icon>
            </div>
            <span>社区疫苗预约管理系统</span>
          </div>
          <p class="footer-desc">便捷预约 · 安全接种 · 健康生活</p>
          <p class="footer-desc">为社区居民提供一站式疫苗预约服务</p>
        </div>

        <!-- 中间快捷链接 -->
        <div class="footer-nav">
          <div class="nav-group">
            <h4>快捷导航</h4>
            <router-link to="/">首页</router-link>
            <router-link to="/vaccine">疫苗接种</router-link>
            <router-link to="/community">社区风采</router-link>
            <router-link to="/news">社区资讯</router-link>
          </div>
          <div class="nav-group">
            <h4>用户服务</h4>
            <router-link to="/my/appointment">我的预约</router-link>
            <router-link to="/my/collection">我的关注</router-link>
            <router-link to="/my/recharge">账户充值</router-link>
            <router-link to="/my/profile">个人信息</router-link>
          </div>
        </div>

        <!-- 右侧联系方式 -->
        <div class="footer-contact">
          <h4>联系我们</h4>
          <div class="contact-item">
            <el-icon><Phone /></el-icon>
            <span>服务热线：400-888-8888</span>
          </div>
          <div class="contact-item">
            <el-icon><Message /></el-icon>
            <span>邮箱：service@vaccine.com</span>
          </div>
          <div class="contact-item">
            <el-icon><Location /></el-icon>
            <span>地址：北京市朝阳区健康路1号</span>
          </div>
        </div>
      </div>


    </footer>

    <!-- 回到顶部 -->
    <el-backtop :right="30" :bottom="30">
      <div class="backtop-btn">
        <el-icon><Top /></el-icon>
      </div>
    </el-backtop>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { HomeFilled, FirstAidKit, OfficeBuilding, Document, Calendar, User, Star, Wallet, SwitchButton, Menu, Top, Phone, Message, Location } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const mobileMenuVisible = ref(false)

// 页面加载时获取最新用户信息（包括余额）
onMounted(async () => {
  if (userStore.isLoggedIn) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      // 获取失败不影响页面显示
    }
  }
})

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/my/profile')
      break
    case 'appointment':
      router.push('/my/appointment')
      break
    case 'collection':
      router.push('/my/collection')
      break
    case 'recharge':
      router.push('/my/recharge')
      break
    case 'logout':
      userStore.logout()
      break
  }
}

const handleLogout = () => {
  mobileMenuVisible.value = false
  userStore.logout()
}

const goLogin = () => {
  mobileMenuVisible.value = false
  router.push('/login')
}

const goRegister = () => {
  mobileMenuVisible.value = false
  router.push('/register')
}
</script>

<style scoped lang="scss">
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-color);
}

// 页面过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 头部导航
.header {
  height: 70px;
  background: linear-gradient(135deg, #fff 0%, #f8fafc 100%);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  backdrop-filter: blur(10px);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: var(--transition);
  
  &:hover {
    transform: scale(1.02);
  }
  
  .logo-icon {
    width: 42px;
    height: 42px;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }
  
  .logo-text {
    font-size: 20px;
    font-weight: 700;
    background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.mobile-menu-btn {
  display: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-primary);
}

.nav {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-regular);
  text-decoration: none;
  font-size: 15px;
  padding: 10px 18px;
  border-radius: 25px;
  transition: var(--transition);
  font-weight: 500;

  .el-icon {
    font-size: 16px;
  }

  &:hover {
    color: var(--primary-color);
    background: rgba(64, 158, 255, 0.1);
  }
  
  &.router-link-active {
    color: #fff;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }
}

.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.balance-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #fef3e2 0%, #fde8d0 100%);
  border-radius: 20px;
  color: #e6a23c;
  font-weight: 600;
  font-size: 14px;
  
  .el-icon {
    font-size: 16px;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px 6px 6px;
  border-radius: 30px;
  transition: var(--transition);
  
  &:hover {
    background: var(--bg-color);
  }
  
  .el-avatar {
    border: 2px solid #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .username {
    color: var(--text-primary);
    font-weight: 500;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .arrow {
    color: var(--text-secondary);
    transition: var(--transition);
  }
}

// 移动端抽屉
.drawer-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: var(--text-primary);
  
  .logo-icon {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }
}

.mobile-menu {
  padding: 0 10px;
}

.mobile-user {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4ff 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  
  .user-detail {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .name {
      font-weight: 600;
      color: var(--text-primary);
    }
    
    .balance {
      font-size: 13px;
      color: #e6a23c;
    }
  }
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  color: var(--text-regular);
  text-decoration: none;
  border-radius: 10px;
  transition: var(--transition);
  font-size: 15px;
  
  .el-icon {
    font-size: 18px;
    color: var(--text-secondary);
  }
  
  &:hover, &.router-link-active {
    background: linear-gradient(135deg, #f0f7ff 0%, #e8f4ff 100%);
    color: var(--primary-color);
    
    .el-icon {
      color: var(--primary-color);
    }
  }
  
  &.logout {
    color: var(--danger-color);
    cursor: pointer;
    
    .el-icon {
      color: var(--danger-color);
    }
  }
}

.nav-divider {
  height: 1px;
  background: var(--border-light);
  margin: 10px 0;
}

.mobile-auth {
  display: flex;
  gap: 12px;
  padding: 20px 0;
  
  .el-button {
    flex: 1;
  }
}

// 主内容
.main {
  flex: 1;
  margin-top: 70px;
}

// 底部
.footer {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  color: rgba(255, 255, 255, 0.85);
  padding-top: 60px;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
  display: grid;
  grid-template-columns: 1.5fr 2fr 1fr;
  gap: 60px;
}

.footer-brand {
  .footer-logo {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    
    .logo-icon {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    
    span {
      font-size: 20px;
      font-weight: 700;
      color: #fff;
    }
  }
  
  .footer-desc {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.6);
    line-height: 1.8;
    margin: 0;
  }
}

.footer-nav {
  display: flex;
  gap: 80px;
  
  .nav-group {
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #fff;
      margin-bottom: 20px;
      position: relative;
      padding-bottom: 10px;
      
      &::after {
        content: '';
        position: absolute;
        left: 0;
        bottom: 0;
        width: 30px;
        height: 2px;
        background: linear-gradient(90deg, #409eff, transparent);
      }
    }
    
    a {
      display: block;
      color: rgba(255, 255, 255, 0.6);
      font-size: 14px;
      margin-bottom: 12px;
      transition: all 0.3s ease;
      text-decoration: none;
      
      &:hover {
        color: #409eff;
        padding-left: 5px;
      }
    }
  }
}

.footer-contact {
  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #fff;
    margin-bottom: 20px;
    position: relative;
    padding-bottom: 10px;
    
    &::after {
      content: '';
      position: absolute;
      left: 0;
      bottom: 0;
      width: 30px;
      height: 2px;
      background: linear-gradient(90deg, #409eff, transparent);
    }
  }
  
  .contact-item {
    display: flex;
    align-items: center;
    gap: 10px;
    color: rgba(255, 255, 255, 0.6);
    font-size: 14px;
    margin-bottom: 15px;
    
    .el-icon {
      color: #409eff;
      font-size: 16px;
    }
  }
}

.footer-bottom {
  margin-top: 50px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 25px 0;
  
  .footer-bottom-content {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .copyright, .tech {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
    margin: 0;
  }
}

// 回到顶部
.backtop-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: var(--transition);
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.5);
  }
}

// 响应式
@media (max-width: 992px) {
  .desktop-nav {
    display: none;
  }
  
  .mobile-menu-btn {
    display: block;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .logo .logo-text {
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .header {
    height: 60px;
  }
  
  .main {
    margin-top: 60px;
  }
  
  .logo .logo-icon {
    width: 36px;
    height: 36px;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .footer-brand .footer-logo {
    justify-content: center;
  }
  
  .footer-nav {
    justify-content: center;
    gap: 40px;
    
    .nav-group h4::after {
      left: 50%;
      transform: translateX(-50%);
    }
  }
  
  .footer-contact {
    h4::after {
      left: 50%;
      transform: translateX(-50%);
    }
    
    .contact-item {
      justify-content: center;
    }
  }
  
  .footer-bottom .footer-bottom-content {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .logo .logo-text {
    display: none;
  }
}
</style>
