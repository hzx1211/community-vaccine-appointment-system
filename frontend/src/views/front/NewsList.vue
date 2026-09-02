<template>
  <div class="news-list-page">
    <div class="content-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1>社区资讯</h1>
          <p class="subtitle">了解最新疫苗动态与健康知识</p>
        </div>
        <div class="header-decoration">
          <el-icon><Reading /></el-icon>
        </div>
      </div>

      <!-- 资讯列表 -->
      <div class="news-grid" v-if="newsList.length">
        <!-- 头条新闻：浏览量最高 -->
        <div v-if="featuredNews" class="featured-news" @click="goDetail(featuredNews.id)">
          <div class="featured-image">
            <img :src="resolveImageSrc(featuredNews.coverImage, 'news')" alt="" />
            <div class="featured-overlay">
              <span class="tag"><el-icon><View /></el-icon> 热门</span>
            </div>
          </div>
          <div class="featured-content">
            <h2>{{ featuredNews.title }}</h2>
            <p class="summary" v-html="stripHtml(featuredNews.summary)"></p>
            <div class="meta">
              <span class="community">
                <el-icon><OfficeBuilding /></el-icon>
                {{ featuredNews.communityName || '系统发布' }}
              </span>
              <span class="date">
                <el-icon><Calendar /></el-icon>
                {{ featuredNews.createTime?.substring(0, 10) }}
              </span>
              <span class="views">
                <el-icon><View /></el-icon>
                {{ featuredNews.viewCount }} 阅读
              </span>
            </div>
          </div>
        </div>

        <!-- 普通新闻列表 -->
        <div class="news-list">
          <div 
            v-for="item in otherNews" 
            :key="item.id" 
            class="news-card"
            @click="goDetail(item.id)"
          >
            <div class="card-image">
              <img :src="resolveImageSrc(item.coverImage, 'news')" alt="" />
              <div class="image-overlay">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
            <div class="card-content">
              <h3 class="title">{{ item.title }}</h3>
              <p class="summary" v-html="stripHtml(item.summary)"></p>
              <div class="card-footer">
                <div class="meta-left">
                  <span class="community">{{ item.communityName || '系统' }}</span>
                  <span class="dot">·</span>
                  <span class="date">{{ formatDate(item.createTime) }}</span>
                </div>
                <div class="meta-right">
                  <el-icon><View /></el-icon>
                  <span>{{ item.viewCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无资讯" :image-size="150" />

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :pager-count="5"
          layout="prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { Reading, OfficeBuilding, Calendar, View, ArrowRight } from '@element-plus/icons-vue'
import { resolveImageSrc } from '@/utils/image'

const router = useRouter()
const newsList = ref([])
const featuredNews = ref(null) // 全站浏览量最高的新闻
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 其他新闻：排除头条
const otherNews = computed(() => {
  if (!featuredNews.value) return newsList.value
  return newsList.value.filter(item => item.id !== featuredNews.value.id)
})

const stripHtml = (html) => {
  if (!html) return ''
  const text = html.replace(/<[^>]+>/g, '')
  return text.length > 120 ? text.substring(0, 120) + '...' : text
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return dateStr.substring(0, 10)
}

// 加载头条新闻（全站浏览量最高）
const loadFeaturedNews = async () => {
  const res = await request.get('/news/top')
  featuredNews.value = res.data
}

const loadData = async () => {
  const res = await request.get('/news/page', {
    params: { status: 1, pageNum: pageNum.value, pageSize: pageSize.value }
  })
  newsList.value = res.data.list
  total.value = res.data.total
}

const goDetail = (id) => {
  router.push(`/news/${id}`)
}

onMounted(() => {
  loadFeaturedNews()
  loadData()
})
</script>

<style scoped lang="scss">
.news-list-page {
  min-height: calc(100vh - 140px);
  padding: 40px 0;
  background: linear-gradient(180deg, #f0f5ff 0%, #f5f7fa 100%);
}

.content-wrapper {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
}

// 页面头部
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 30px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -10%;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }

  .header-content {
    position: relative;
    z-index: 1;

    h1 {
      font-size: 28px;
      font-weight: 600;
      margin: 0 0 8px;
    }

    .subtitle {
      font-size: 14px;
      opacity: 0.9;
      margin: 0;
    }
  }

  .header-decoration {
    position: relative;
    z-index: 1;
    font-size: 48px;
    opacity: 0.8;
  }
}

// 头条新闻
.featured-news {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);

    .featured-image img {
      transform: scale(1.05);
    }

    h2 {
      color: #667eea;
    }
  }

  .featured-image {
    width: 45%;
    min-height: 280px;
    position: relative;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }

    .featured-overlay {
      position: absolute;
      top: 16px;
      left: 16px;

      .tag {
        background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
        color: #fff;
        padding: 6px 16px;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 500;
      }
    }
  }

  .featured-content {
    flex: 1;
    padding: 30px;
    display: flex;
    flex-direction: column;

    h2 {
      font-size: 22px;
      color: #1a1a2e;
      margin: 0 0 16px;
      line-height: 1.4;
      transition: color 0.3s;
    }

    .summary {
      font-size: 15px;
      color: #666;
      line-height: 1.8;
      flex: 1;
      margin: 0;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .meta {
      display: flex;
      gap: 24px;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #f0f0f0;

      span {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: #888;

        .el-icon {
          font-size: 15px;
        }
      }
    }
  }
}

// 新闻列表
.news-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.news-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);

    .card-image img {
      transform: scale(1.1);
    }

    .image-overlay {
      opacity: 1;
    }

    .title {
      color: #667eea;
    }
  }

  .card-image {
    height: 160px;
    position: relative;
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
      background: rgba(102, 126, 234, 0.7);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;

      .el-icon {
        font-size: 32px;
        color: #fff;
      }
    }
  }

  .card-content {
    padding: 20px;

    .title {
      font-size: 16px;
      font-weight: 600;
      color: #1a1a2e;
      margin: 0 0 10px;
      line-height: 1.5;
      transition: color 0.3s;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      min-height: 48px;
    }

    .summary {
      font-size: 13px;
      color: #888;
      line-height: 1.6;
      margin: 0 0 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      min-height: 42px;
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 12px;
      border-top: 1px solid #f5f5f5;

      .meta-left {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: #999;

        .community {
          color: #667eea;
          font-weight: 500;
        }

        .dot {
          color: #ddd;
        }
      }

      .meta-right {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #bbb;

        .el-icon {
          font-size: 14px;
        }
      }
    }
  }
}

// 分页
.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;

  :deep(.el-pagination) {
    .el-pager li {
      border-radius: 8px;
      margin: 0 4px;

      &.is-active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }

    .btn-prev, .btn-next {
      border-radius: 8px;
    }
  }
}

// 响应式
@media (max-width: 992px) {
  .news-list {
    grid-template-columns: repeat(2, 1fr);
  }

  .featured-news {
    flex-direction: column;

    .featured-image {
      width: 100%;
      min-height: 200px;
    }
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px;

    .header-content h1 {
      font-size: 22px;
    }

    .header-decoration {
      font-size: 36px;
    }
  }

  .news-list {
    grid-template-columns: 1fr;
  }

  .featured-content {
    padding: 20px !important;

    h2 {
      font-size: 18px !important;
    }
  }
}
</style>
