<template>
  <div class="home-page">
    <div class="content-wrapper">
      <!-- 轮播图 -->
      <section class="banner-section">
        <el-carousel height="400px" v-if="homeData.banners?.length" :interval="5000" arrow="hover">
          <el-carousel-item v-for="item in homeData.banners" :key="item.id">
            <div class="banner-item">
              <img :src="resolveImageSrc(item.image, 'banner')" :alt="item.title" class="banner-img" />
              <div class="banner-overlay">
                <div class="banner-content">
                  <h2>{{ item.title || '欢迎使用社区疫苗预约系统' }}</h2>
                  <p>便捷预约 · 安全接种 · 健康生活</p>
                  <el-button type="primary" size="large" round @click="router.push('/vaccine')">
                    立即预约 <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
        <div class="banner-placeholder" v-else>
          <div class="placeholder-content">
            <el-icon size="60"><FirstAidKit /></el-icon>
            <h2>社区疫苗预约管理系统</h2>
            <p>便捷预约 · 安全接种 · 健康生活</p>
            <el-button type="primary" size="large" round @click="router.push('/vaccine')">
              立即预约 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </section>

      <!-- 快捷入口 -->
      <section class="quick-entry">
        <div class="entry-wrapper">
          <div class="entry-item" @click="router.push('/vaccine')">
            <div class="entry-icon vaccine">
              <el-icon><FirstAidKit /></el-icon>
            </div>
            <span>疫苗预约</span>
          </div>
          <div class="entry-item" @click="router.push('/community')">
            <div class="entry-icon community">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <span>社区风采</span>
          </div>
          <div class="entry-item" @click="router.push('/news')">
            <div class="entry-icon news">
              <el-icon><Document /></el-icon>
            </div>
            <span>健康资讯</span>
          </div>
          <div class="entry-item" @click="router.push('/my/appointment')">
            <div class="entry-icon appointment">
              <el-icon><Calendar /></el-icon>
            </div>
            <span>我的预约</span>
          </div>
        </div>
      </section>

      <!-- 热门疫苗 -->
      <section class="section vaccine-section">
        <div class="section-header">
          <div class="title-group">
            <h2><el-icon><FirstAidKit /></el-icon>热门疫苗</h2>
            <p class="subtitle">为您推荐最受欢迎的疫苗</p>
          </div>
          <router-link to="/vaccine" class="more-btn">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="vaccine-grid">
          <div v-for="(item, index) in homeData.hotVaccines" :key="item.id" 
               class="vaccine-card" 
               @click="router.push(`/vaccine/${item.id}`)">
            <div class="card-badge" v-if="index < 3">TOP{{ index + 1 }}</div>
            <div class="card-image">
              <img :src="resolveImageSrc(item.image, 'vaccine')" />
              <div class="image-overlay">
                <el-button type="primary" round>立即预约</el-button>
              </div>
            </div>
            <div class="card-content">
              <h3 class="name">{{ item.name }}</h3>
              <p class="manufacturer">
                <el-icon><OfficeBuilding /></el-icon>
                {{ item.manufacturer }}
              </p>
              <div class="card-footer">
                <span class="price">¥<em>{{ item.price }}</em></span>
                <span class="count">
                  <el-icon><User /></el-icon>
                  {{ item.appointmentCount }}人已约
                </span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-if="!homeData.hotVaccines?.length" description="暂无疫苗信息" />
      </section>

      <div class="two-columns">
        <!-- 最新资讯 -->
        <section class="section news-section">
          <div class="section-header">
            <div class="title-group">
              <h2><el-icon><Document /></el-icon>最新资讯</h2>
            </div>
            <router-link to="/news" class="more-btn">
              更多 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div class="news-list">
            <div v-for="item in newsList" :key="item.id" class="news-item" @click="router.push(`/news/${item.id}`)">
              <div class="news-dot"></div>
              <div class="news-content">
                <span class="title">{{ item.title }}</span>
                <span class="date">
                  <el-icon><Clock /></el-icon>
                  {{ item.createTime?.substring(0, 10) }}
                </span>
              </div>
            </div>
            <el-empty v-if="!newsList.length" description="暂无资讯" :image-size="80" />
          </div>
          <div class="list-pagination" v-if="newsTotal > newsPageSize">
            <el-pagination
              v-model:current-page="newsPageNum"
              :page-size="newsPageSize"
              :total="newsTotal"
              layout="prev, pager, next"
              small
              @current-change="loadNews"
            />
          </div>
        </section>

        <!-- 系统公告 -->
        <section class="section announcement-section">
          <div class="section-header">
            <div class="title-group">
              <h2><el-icon><Bell /></el-icon>系统公告</h2>
            </div>
          </div>
          <div class="announcement-list">
            <div v-for="item in announcementList" :key="item.id" class="announcement-item" @click="showAnnouncement(item)">
              <div class="announcement-icon">
                <el-icon><Bell /></el-icon>
              </div>
              <div class="announcement-content">
                <span class="title">{{ item.title }}</span>
                <span class="date">{{ item.createTime?.substring(0, 10) }}</span>
              </div>
            </div>
            <el-empty v-if="!announcementList.length" description="暂无公告" :image-size="80" />
          </div>
          <div class="list-pagination" v-if="announcementTotal > announcementPageSize">
            <el-pagination
              v-model:current-page="announcementPageNum"
              :page-size="announcementPageSize"
              :total="announcementTotal"
              layout="prev, pager, next"
              small
              @current-change="loadAnnouncements"
            />
          </div>
        </section>
      </div>

      <!-- 公告详情弹窗 -->
      <el-dialog v-model="announcementDialogVisible" :title="currentAnnouncement.title" width="600px" class="announcement-dialog">
        <div class="announcement-detail">
          <div class="detail-meta">
            <span><el-icon><Clock /></el-icon> {{ currentAnnouncement.createTime?.substring(0, 16) }}</span>
          </div>
          <div class="detail-content">{{ currentAnnouncement.content }}</div>
        </div>
      </el-dialog>

      <!-- 统计数据 -->
      <section class="stats-section">
        <div class="stat-item">
          <div class="stat-icon">
            <el-icon><FirstAidKit /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.vaccineCount || 0 }}</span>
            <span class="stat-label">疫苗种类</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.userCount || 0 }}</span>
            <span class="stat-label">注册用户</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.appointmentCount || 0 }}</span>
            <span class="stat-label">预约总数</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.communityCount || 0 }}</span>
            <span class="stat-label">服务社区</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHomeData } from '@/api/public'
import request from '@/utils/request'
import { resolveImageSrc } from '@/utils/image'
import { FirstAidKit, OfficeBuilding, Document, Calendar, User, Bell, Clock, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const homeData = ref({})
const stats = ref({ vaccineCount: 12, userCount: 1280, appointmentCount: 3560, communityCount: 8 })

// 资讯分页
const newsList = ref([])
const newsPageNum = ref(1)
const newsPageSize = ref(5)
const newsTotal = ref(0)

// 公告分页
const announcementList = ref([])
const announcementPageNum = ref(1)
const announcementPageSize = ref(5)
const announcementTotal = ref(0)
const announcementDialogVisible = ref(false)
const currentAnnouncement = ref({})

const loadNews = async () => {
  const res = await request.get('/news/page', {
    params: { status: 1, pageNum: newsPageNum.value, pageSize: newsPageSize.value }
  })
  newsList.value = res.data.list
  newsTotal.value = res.data.total
}

const loadAnnouncements = async () => {
  const res = await request.get('/announcement/page', {
    params: { status: 1, pageNum: announcementPageNum.value, pageSize: announcementPageSize.value }
  })
  announcementList.value = res.data.list
  announcementTotal.value = res.data.total
}

const showAnnouncement = (item) => {
  currentAnnouncement.value = item
  announcementDialogVisible.value = true
}

onMounted(async () => {
  const res = await getHomeData()
  homeData.value = res.data
  loadNews()
  loadAnnouncements()
})
</script>

<style scoped lang="scss">
.home-page {
  min-height: calc(100vh - 140px);
  background: var(--bg-color);
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 20px 40px;
}

// 轮播图
.banner-section {
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  
  :deep(.el-carousel__indicators) {
    bottom: 20px;
    
    .el-carousel__indicator button {
      width: 30px;
      height: 4px;
      border-radius: 2px;
      background: rgba(255, 255, 255, 0.5);
    }
    
    .el-carousel__indicator.is-active button {
      background: #fff;
    }
  }
  
  :deep(.el-carousel__arrow) {
    background: rgba(255, 255, 255, 0.3);
    backdrop-filter: blur(4px);
    
    &:hover {
      background: rgba(255, 255, 255, 0.5);
    }
  }
}

.banner-item {
  position: relative;
  height: 100%;
  
  &:hover .banner-overlay {
    opacity: 1;
  }
  
  &:hover .banner-content {
    opacity: 1;
    transform: translateY(0);
  }
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.75) 0%, rgba(102, 126, 234, 0.75) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.banner-content {
  text-align: center;
  color: #fff;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.4s ease;
  
  h2 {
    font-size: 38px;
    font-weight: 700;
    margin-bottom: 16px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  }
  
  p {
    font-size: 18px;
    opacity: 0.9;
    letter-spacing: 4px;
    margin-bottom: 28px;
  }
  
  .el-button {
    padding: 14px 36px;
    font-size: 16px;
  }
}

.banner-placeholder {
  height: 400px;
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  
  .placeholder-content {
    text-align: center;
    color: #fff;
    
    .el-icon {
      margin-bottom: 20px;
      opacity: 0.9;
    }
    
    h2 {
      font-size: 32px;
      margin-bottom: 12px;
    }
    
    p {
      font-size: 16px;
      opacity: 0.8;
      letter-spacing: 3px;
      margin-bottom: 28px;
    }
    
    .el-button {
      padding: 14px 36px;
      font-size: 16px;
    }
  }
}

// 快捷入口
.quick-entry {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.entry-wrapper {
  display: flex;
  justify-content: space-around;
  gap: 20px;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    
    .entry-icon {
      transform: scale(1.1);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    }
  }
  
  span {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }
}

.entry-icon {
  width: 70px;
  height: 70px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  
  &.vaccine { background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%); }
  &.community { background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%); }
  &.news { background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%); }
  &.appointment { background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%); }
}

.section {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  .title-group {
    h2 {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 22px;
      color: #303133;
      font-weight: 600;
      
      .el-icon {
        color: #409eff;
      }
    }
    
    .subtitle {
      font-size: 13px;
      color: #909399;
      margin-top: 6px;
      margin-left: 32px;
    }
  }
  
  .more-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #409eff;
    font-size: 14px;
    padding: 8px 16px;
    border-radius: 20px;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(64, 158, 255, 0.1);
    }
  }
}

// 疫苗卡片
.vaccine-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.vaccine-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    border-color: transparent;
    
    .image-overlay {
      opacity: 1;
    }
    
    .card-image img {
      transform: scale(1.1);
    }
  }
  
  .card-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
    color: #fff;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    z-index: 10;
  }
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
  
  .image-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: all 0.3s ease;
  }
}

.card-content {
  padding: 18px;
  
  .name {
    font-size: 17px;
    color: #303133;
    font-weight: 600;
    margin-bottom: 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .manufacturer {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #909399;
    margin-bottom: 14px;
  }
  
  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 14px;
    border-top: 1px dashed #e4e7ed;
  }
  
  .price {
    color: #f56c6c;
    font-size: 14px;
    
    em {
      font-size: 22px;
      font-weight: 700;
      font-style: normal;
    }
  }
  
  .count {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #909399;
  }
}

// 两列布局
.two-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

// 资讯列表
.news-list {
  .news-item {
    display: flex;
    align-items: flex-start;
    gap: 14px;
    padding: 16px 0;
    border-bottom: 1px solid #e4e7ed;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover .title {
      color: #409eff;
    }
  }
  
  .news-dot {
    width: 8px;
    height: 8px;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    border-radius: 50%;
    margin-top: 7px;
    flex-shrink: 0;
  }
  
  .news-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 10px;
    
    .title {
      color: #303133;
      font-size: 15px;
      transition: all 0.3s ease;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .date {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #909399;
      font-size: 13px;
      flex-shrink: 0;
    }
  }
}

// 公告列表
.announcement-list {
  .announcement-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 16px 0;
    border-bottom: 1px solid #e4e7ed;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover {
      .title {
        color: #e6a23c;
      }
    }
  }
  
  .announcement-icon {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #fef3e2 0%, #fde8d0 100%);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #e6a23c;
    flex-shrink: 0;
  }
  
  .announcement-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .title {
      color: #303133;
      font-size: 15px;
      transition: color 0.3s;
    }
    
    .date {
      color: #909399;
      font-size: 13px;
    }
  }
}

// 公告详情弹窗
.announcement-dialog {
  :deep(.el-dialog__header) {
    border-bottom: 1px solid #e4e7ed;
    padding-bottom: 16px;
    margin-right: 0;
    
    .el-dialog__title {
      font-weight: 600;
      color: #303133;
    }
  }
  
  :deep(.el-dialog__body) {
    padding-top: 20px;
  }
}

.announcement-detail {
  .detail-meta {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #909399;
    font-size: 13px;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px dashed #e4e7ed;
    
    .el-icon {
      font-size: 14px;
    }
  }
  
  .detail-content {
    color: #606266;
    font-size: 15px;
    line-height: 1.8;
    white-space: pre-wrap;
  }
}

// 列表分页
.list-pagination {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: center;
  
  :deep(.el-pagination) {
    .el-pager li {
      min-width: 28px;
      height: 28px;
      line-height: 28px;
      border-radius: 4px;
      
      &.is-active {
        background: #409eff;
      }
    }
    
    .btn-prev, .btn-next {
      min-width: 28px;
      height: 28px;
      border-radius: 4px;
    }
  }
}

// 统计数据
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-item {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  }
  
  &:nth-child(1) .stat-icon { background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%); }
  &:nth-child(2) .stat-icon { background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%); }
  &:nth-child(3) .stat-icon { background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%); }
  &:nth-child(4) .stat-icon { background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%); }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  
  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
  }
  
  .stat-label {
    font-size: 14px;
    color: #909399;
  }
}

// 响应式
@media (max-width: 1200px) {
  .vaccine-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 992px) {
  .two-columns {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px 16px;
  }
  
  .banner-section {
    border-radius: 12px;
    
    :deep(.el-carousel) {
      height: 280px !important;
    }
  }
  
  .banner-content {
    h2 { font-size: 24px; }
    p { font-size: 14px; letter-spacing: 2px; margin-bottom: 20px; }
    
    .el-button {
      padding: 10px 24px;
      font-size: 14px;
    }
  }
  
  .banner-placeholder {
    height: 280px;
    
    .placeholder-content {
      h2 { font-size: 22px; }
      p { font-size: 13px; margin-bottom: 20px; }
    }
  }
  
  .quick-entry {
    padding: 20px;
    border-radius: 12px;
  }
  
  .entry-wrapper {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .entry-item {
    width: calc(50% - 8px);
  }
  
  .entry-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
    border-radius: 12px;
  }
  
  .entry-item span {
    font-size: 13px;
  }
  
  .vaccine-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-section {
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  
  .stat-item {
    padding: 20px;
    border-radius: 12px;
    
    .stat-icon {
      width: 46px;
      height: 46px;
      font-size: 20px;
      border-radius: 12px;
    }
    
    .stat-value {
      font-size: 22px;
    }
  }
  
  .section {
    padding: 20px;
    border-radius: 12px;
  }
  
  .section-header .title-group h2 {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .content-wrapper {
    padding: 16px 12px;
  }
  
  .banner-section :deep(.el-carousel) {
    height: 220px !important;
  }
  
  .banner-content h2 {
    font-size: 20px;
  }
  
  .stats-section {
    gap: 12px;
  }
  
  .stat-item {
    padding: 16px;
    gap: 12px;
    
    .stat-icon {
      width: 40px;
      height: 40px;
      font-size: 18px;
    }
    
    .stat-value {
      font-size: 20px;
    }
  }
}
</style>
