<template>
  <div class="community-detail-page">
    <div class="content-wrapper" v-if="community">
      <div class="breadcrumb">
        <router-link to="/community">社区风采</router-link>
        <span class="separator">/</span>
        <span>{{ community.name }}</span>
      </div>

      <div class="detail-card">
        <div class="card-header">
          <img :src="resolveImageSrc(community.image, 'community')" class="cover-image" />
          <div class="header-info">
            <h1 class="name">{{ community.name }}</h1>
            <div class="info-list">
              <p class="info-item">
                <el-icon><Phone /></el-icon>
                <span>联系电话：{{ community.contactPhone || '暂无' }}</span>
              </p>
              <p class="info-item">
                <el-icon><Location /></el-icon>
                <span>社区地址：{{ community.address || '暂无' }}</span>
              </p>
            </div>
          </div>
        </div>

        <div class="card-body">
          <h2 class="section-title">社区介绍</h2>
          <div class="description" v-html="sanitizeRichHtml(community.description, '暂无介绍')"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'
import { resolveImageSrc } from '@/utils/image'
import { sanitizeRichHtml } from '@/utils/sanitize'

const route = useRoute()
const community = ref(null)

const loadData = async () => {
  const id = route.params.id
  const res = await request.get(`/community/${id}`)
  community.value = res.data
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.community-detail-page {
  min-height: calc(100vh - 140px);
  padding: 30px 0;
  background: #f5f7fa;
}

.content-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.breadcrumb {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
  
  a {
    color: #409eff;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  .separator {
    margin: 0 8px;
    color: #999;
  }
}

.detail-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  display: flex;
  padding: 30px;
  background: linear-gradient(135deg, #5cbdb9 0%, #4aa9a5 100%);
  
  .cover-image {
    width: 200px;
    height: 150px;
    object-fit: cover;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  
  .header-info {
    flex: 1;
    margin-left: 30px;
    color: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    
    .name {
      font-size: 28px;
      margin-bottom: 20px;
    }
    
    .info-list {
      .info-item {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 10px;
        font-size: 15px;
        opacity: 0.95;
      }
    }
  }
}

.card-body {
  padding: 30px;
  
  .section-title {
    font-size: 18px;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #5cbdb9;
    display: inline-block;
  }
  
  .description {
    line-height: 1.8;
    color: #666;
    font-size: 15px;
    
    :deep(img) {
      max-width: 100%;
      height: auto;
    }
    
    :deep(p) {
      margin-bottom: 15px;
    }
  }
}
</style>
