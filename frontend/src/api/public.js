// 公开接口API
import request from '@/utils/request'

// 获取首页数据
export function getHomeData() {
  return request.get('/public/home')
}

// 获取轮播图
export function getBanners() {
  return request.get('/public/banners')
}

// 获取热门疫苗
export function getHotVaccines(limit = 6) {
  return request.get('/public/hot-vaccines', { params: { limit } })
}

// 获取疫苗分类
export function getVaccineCategories() {
  return request.get('/public/vaccine-categories')
}

// 获取最新资讯
export function getLatestNews(limit = 5) {
  return request.get('/public/latest-news', { params: { limit } })
}

// 获取系统公告
export function getAnnouncements() {
  return request.get('/public/announcements')
}

// 获取社区列表
export function getCommunities() {
  return request.get('/public/communities')
}
