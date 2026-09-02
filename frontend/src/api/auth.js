// 认证相关API
import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request.post('/auth/login', data)
}

// 用户注册
export function register(data) {
  return request.post('/auth/register', data)
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/auth/info')
}

// 修改密码
export function updatePassword(data) {
  return request.post('/auth/password', data)
}

// 更新个人信息
export function updateProfile(data) {
  return request.put('/auth/profile', data)
}
