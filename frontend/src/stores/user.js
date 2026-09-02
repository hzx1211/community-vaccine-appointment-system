// 用户状态管理
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'admin')
  const isCommunityAdmin = computed(() => userInfo.value.role === 'community_admin')
  const isUser = computed(() => userInfo.value.role === 'user')

  // 登录
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.token
    userInfo.value = res.data
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res.data
  }

  // 获取用户信息
  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = { ...userInfo.value, ...res.data }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    return res.data
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isCommunityAdmin,
    isUser,
    login,
    fetchUserInfo,
    logout
  }
})
