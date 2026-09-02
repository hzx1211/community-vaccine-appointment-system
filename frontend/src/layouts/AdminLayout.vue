<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <div class="logo-icon">
          <el-icon size="22"><FirstAidKit /></el-icon>
        </div>
        <transition name="fade">
          <span class="logo-text" v-show="!isCollapsed">疫苗管理系统</span>
        </transition>
      </div>
      
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="route.path"
          :collapse="isCollapsed"
          :collapse-transition="false"
          router
          background-color="transparent"
          text-color="rgba(255,255,255,0.7)"
          active-text-color="#fff"
        >
          <el-menu-item index="/admin">
            <el-icon><DataAnalysis /></el-icon>
            <span>控制台</span>
          </el-menu-item>
          
          <!-- 管理员：疫苗管理（含分类） -->
          <template v-if="userStore.isAdmin">
            <el-sub-menu index="vaccine-group">
              <template #title>
                <el-icon><FirstAidKit /></el-icon>
                <span>疫苗管理</span>
              </template>
              <el-menu-item index="/admin/vaccine">疫苗信息</el-menu-item>
              <el-menu-item index="/admin/vaccine-category">疫苗分类</el-menu-item>
            </el-sub-menu>
          </template>

          <!-- 社区管理员：疫苗信息（只读） -->
          <template v-if="userStore.isCommunityAdmin">
            <el-menu-item index="/admin/vaccine">
              <el-icon><FirstAidKit /></el-icon>
              <span>疫苗信息</span>
            </el-menu-item>
          </template>

          <el-menu-item index="/admin/appointment">
            <el-icon><Calendar /></el-icon>
            <span>预约管理</span>
          </el-menu-item>

          <!-- 社区管理员：我的社区 -->
          <template v-if="userStore.isCommunityAdmin">
            <el-menu-item index="/admin/my-community">
              <el-icon><OfficeBuilding /></el-icon>
              <span>我的社区</span>
            </el-menu-item>
          </template>

          <!-- 管理员：社区管理 -->
          <template v-if="userStore.isAdmin">
            <el-menu-item index="/admin/community">
              <el-icon><OfficeBuilding /></el-icon>
              <span>社区管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/news">
              <el-icon><Document /></el-icon>
              <span>资讯管理</span>
            </el-menu-item>

            <el-sub-menu index="user-group">
              <template #title>
                <el-icon><Grid /></el-icon>
                <span>用户管理</span>
              </template>
              <el-menu-item index="/admin/user/admin">管理员信息</el-menu-item>
              <el-menu-item index="/admin/user/community-admin">社区管理员</el-menu-item>
              <el-menu-item index="/admin/user/normal">普通用户信息</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="system-group">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/admin/banner">轮播图管理</el-menu-item>
              <el-menu-item index="/admin/announcement">公告管理</el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/admin/recharge">
              <el-icon><Wallet /></el-icon>
              <span>充值记录</span>
            </el-menu-item>

            <el-menu-item index="/admin/collection">
              <el-icon><Star /></el-icon>
              <span>用户收藏</span>
            </el-menu-item>

            <el-menu-item index="/admin/statistics">
              <el-icon><TrendCharts /></el-icon>
              <span>数据统计</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
      
      <!-- 侧边栏底部 -->
      <div class="sidebar-footer" v-show="!isCollapsed">
        <div class="version">v1.0.0</div>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <div class="main-container">
      <!-- 顶部栏 -->
      <header class="header">
        <div class="header-left">
          <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <el-icon :size="20">
              <Fold v-if="!isCollapsed" />
              <Expand v-else />
            </el-icon>
          </div>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">
              <el-icon><HomeFilled /></el-icon>
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 全屏按钮 -->
          <div class="header-action" @click="toggleFullscreen">
            <el-icon :size="18"><FullScreen /></el-icon>
          </div>
          
          <!-- 用户信息 -->
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-dropdown">
              <el-avatar :size="36" :src="userStore.userInfo.avatar">
                {{ userStore.userInfo.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <div class="user-detail">
                <span class="user-name">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
                <span class="user-role">{{ roleText }}</span>
              </div>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 主内容 -->
      <main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { HomeFilled, FullScreen, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()
const isCollapsed = ref(false)

const roleText = computed(() => {
  const roles = { admin: '系统管理员', community_admin: '社区管理员' }
  return roles[userStore.userInfo.role] || '管理员'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
  }
}

const toggleFullscreen = () => {
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    document.documentElement.requestFullscreen()
  }
}
</script>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  height: 100vh;
  background: var(--bg-color);
}

// 页面过渡动画
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 侧边栏
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1e3a5f 0%, #152238 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 100;
  
  &.collapsed {
    width: 64px;
    
    .logo {
      padding: 0;
      justify-content: center;
    }
    
    .logo-icon {
      margin: 0;
    }
  }
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: rgba(0, 0, 0, 0.15);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  
  .logo-icon {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }
  
  .logo-text {
    margin-left: 12px;
    font-size: 17px;
    font-weight: 600;
    color: #fff;
    white-space: nowrap;
  }
}

.menu-scrollbar {
  flex: 1;
  
  :deep(.el-scrollbar__view) {
    padding: 10px 0;
  }
}

// 菜单样式
:deep(.el-menu) {
  border: none;
  
  .el-menu-item,
  .el-sub-menu__title {
    height: 50px;
    line-height: 50px;
    margin: 4px 8px;
    border-radius: 8px;
    transition: all 0.3s;
    
    &:hover {
      background: rgba(255, 255, 255, 0.08) !important;
    }
    
    .el-icon {
      font-size: 18px;
      margin-right: 10px;
    }
  }
  
  .el-menu-item.is-active {
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%) !important;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 24px;
      background: #fff;
      border-radius: 0 4px 4px 0;
    }
  }
  
  .el-sub-menu {
    .el-menu-item {
      padding-left: 54px !important;
      height: 44px;
      line-height: 44px;
      font-size: 14px;
      
      &.is-active {
        background: rgba(64, 158, 255, 0.2) !important;
        color: #409eff !important;
        box-shadow: none;
        
        &::before {
          display: none;
        }
      }
    }
  }
  
  &.el-menu--collapse {
    .el-menu-item,
    .el-sub-menu__title {
      margin: 4px;
      padding: 0 !important;
      justify-content: center;
      
      .el-icon {
        margin: 0;
      }
    }
  }
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  
  .version {
    text-align: center;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.3);
  }
}

// 主容器
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

// 顶部栏
.header {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  position: relative;
  z-index: 90;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-regular);
  transition: var(--transition);
  
  &:hover {
    background: var(--bg-color);
    color: var(--primary-color);
  }
}

:deep(.el-breadcrumb) {
  .el-breadcrumb__item {
    .el-breadcrumb__inner {
      color: var(--text-secondary);
      font-weight: 500;
      
      &:hover {
        color: var(--primary-color);
      }
    }
    
    &:last-child .el-breadcrumb__inner {
      color: var(--text-primary);
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-action {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: var(--transition);
  
  &:hover {
    background: var(--bg-color);
    color: var(--primary-color);
  }
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px 6px 6px;
  border-radius: 30px;
  cursor: pointer;
  transition: var(--transition);
  
  &:hover {
    background: var(--bg-color);
  }
  
  .el-avatar {
    border: 2px solid #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .user-detail {
    display: flex;
    flex-direction: column;
    line-height: 1.3;
    
    .user-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-primary);
    }
    
    .user-role {
      font-size: 12px;
      color: var(--text-secondary);
    }
  }
  
  .arrow {
    color: var(--text-secondary);
    font-size: 12px;
  }
}

// 主内容区
.main {
  flex: 1;
  padding: 24px;
  overflow: auto;
  background: var(--bg-color);
}

// 响应式
@media (max-width: 992px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
    
    &.collapsed {
      transform: translateX(-100%);
    }
  }
  
  .main-container {
    margin-left: 0;
  }
  
  .user-detail {
    display: none !important;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }
  
  .main {
    padding: 16px;
  }
  
  :deep(.el-breadcrumb) {
    display: none;
  }
}
</style>
