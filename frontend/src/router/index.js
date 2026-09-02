// 路由配置
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  // 前台用户页面
  {
    path: '/',
    component: () => import('@/layouts/FrontLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/front/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'vaccine',
        name: 'VaccineList',
        component: () => import('@/views/front/VaccineList.vue'),
        meta: { title: '疫苗接种' }
      },
      {
        path: 'vaccine/:id',
        name: 'VaccineDetail',
        component: () => import('@/views/front/VaccineDetail.vue'),
        meta: { title: '疫苗详情' }
      },
      {
        path: 'community',
        name: 'CommunityList',
        component: () => import('@/views/front/CommunityList.vue'),
        meta: { title: '社区风采' }
      },
      {
        path: 'community/:id',
        name: 'CommunityDetail',
        component: () => import('@/views/front/CommunityDetail.vue'),
        meta: { title: '社区详情' }
      },
      {
        path: 'news',
        name: 'NewsList',
        component: () => import('@/views/front/NewsList.vue'),
        meta: { title: '社区资讯' }
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('@/views/front/NewsDetail.vue'),
        meta: { title: '资讯详情' }
      },
      {
        path: 'my/appointment',
        name: 'MyAppointment',
        component: () => import('@/views/front/MyAppointment.vue'),
        meta: { title: '我的预约', requireAuth: true }
      },
      {
        path: 'my/collection',
        name: 'MyCollection',
        component: () => import('@/views/front/MyCollection.vue'),
        meta: { title: '我的关注', requireAuth: true }
      },
      {
        path: 'my/recharge',
        name: 'MyRecharge',
        component: () => import('@/views/front/MyRecharge.vue'),
        meta: { title: '我的充值', requireAuth: true }
      },
      {
        path: 'my/profile',
        name: 'MyProfile',
        component: () => import('@/views/front/MyProfile.vue'),
        meta: { title: '个人信息', requireAuth: true }
      }
    ]
  },
  // 后台管理页面
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requireAuth: true, roles: ['admin', 'community_admin'] },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'vaccine',
        name: 'AdminVaccine',
        component: () => import('@/views/admin/VaccineManage.vue'),
        meta: { title: '疫苗管理', roles: ['admin', 'community_admin'] }
      },
      {
        path: 'vaccine-category',
        name: 'AdminVaccineCategory',
        component: () => import('@/views/admin/VaccineCategoryManage.vue'),
        meta: { title: '疫苗分类', roles: ['admin'] }
      },
      {
        path: 'appointment',
        name: 'AdminAppointment',
        component: () => import('@/views/admin/AppointmentManage.vue'),
        meta: { title: '预约管理', roles: ['admin', 'community_admin'] }
      },
      {
        path: 'community',
        name: 'AdminCommunity',
        component: () => import('@/views/admin/CommunityManage.vue'),
        meta: { title: '社区管理', roles: ['admin'] }
      },
      {
        path: 'my-community',
        name: 'AdminMyCommunity',
        component: () => import('@/views/admin/MyCommunityManage.vue'),
        meta: { title: '我的社区', roles: ['community_admin'] }
      },
      {
        path: 'news',
        name: 'AdminNews',
        component: () => import('@/views/admin/NewsManage.vue'),
        meta: { title: '资讯管理', roles: ['admin'] }
      },
      {
        path: 'user/admin',
        name: 'AdminUserAdmin',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '管理员信息', roles: ['admin'], userRole: 'admin' }
      },
      {
        path: 'user/community-admin',
        name: 'AdminUserCommunityAdmin',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '社区管理员', roles: ['admin'], userRole: 'community_admin' }
      },
      {
        path: 'user/normal',
        name: 'AdminUserNormal',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '普通用户信息', roles: ['admin'], userRole: 'user' }
      },
      {
        path: 'banner',
        name: 'AdminBanner',
        component: () => import('@/views/admin/BannerManage.vue'),
        meta: { title: '轮播图管理', roles: ['admin'] }
      },
      {
        path: 'announcement',
        name: 'AdminAnnouncement',
        component: () => import('@/views/admin/AnnouncementManage.vue'),
        meta: { title: '公告管理', roles: ['admin'] }
      },
      {
        path: 'recharge',
        name: 'AdminRecharge',
        component: () => import('@/views/admin/RechargeManage.vue'),
        meta: { title: '充值记录', roles: ['admin'] }
      },
      {
        path: 'collection',
        name: 'AdminCollection',
        component: () => import('@/views/admin/CollectionManage.vue'),
        meta: { title: '用户收藏', roles: ['admin'] }
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计', roles: ['admin'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 社区疫苗预约管理系统` : '社区疫苗预约管理系统'
  
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const isAdmin = userInfo.role === 'admin' || userInfo.role === 'community_admin'
  const isUser = userInfo.role === 'user'
  
  // 登录和注册页面：已登录用户根据角色跳转
  if (to.path === '/login' || to.path === '/register') {
    if (token && userInfo.role) {
      next(isAdmin ? '/admin' : '/')
      return
    }
    next()
    return
  }
  
  // 后台路由：只允许管理员和社区管理员访问
  if (to.path.startsWith('/admin')) {
    if (!token) {
      next('/login')
      return
    }
    if (isUser) {
      next('/')
      return
    }
  }
  
  // 前台路由：管理员不能访问前台（登录注册除外）
  if (!to.path.startsWith('/admin') && to.path !== '/login' && to.path !== '/register') {
    if (token && isAdmin) {
      next('/admin')
      return
    }
  }
  
  // 需要登录但未登录
  if (to.meta.requireAuth && !token) {
    next('/login')
    return
  }
  
  // 特定角色权限检查
  if (to.meta.roles && !to.meta.roles.includes(userInfo.role)) {
    next(isAdmin ? '/admin' : '/')
    return
  }
  
  next()
})

export default router
