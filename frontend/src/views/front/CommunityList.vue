<template>
  <div class="community-list-page">
    <div class="content-wrapper">
      <div class="page-header">
        <h2>社区风采</h2>
      </div>
      
      <div class="community-grid">
        <div v-for="item in communities" :key="item.id" class="community-card">
          <div class="card-image" @click="goDetail(item.id)">
            <img :src="resolveImageSrc(item.image, 'community')" />
          </div>
          <div class="card-content">
            <h3 class="name" @click="goDetail(item.id)">{{ item.name }}</h3>
            <p class="info-item">
              <span class="label">电话：</span>{{ item.contactPhone || '暂无' }}
            </p>
            <p class="info-item">
              <span class="label">地址：</span>{{ item.address || '暂无' }}
            </p>
            <el-button type="primary" size="small" @click="goDetail(item.id)">社区详细介绍</el-button>
          </div>
        </div>
      </div>

      <div class="pagination">
        <span class="total">共 {{ total }} 条</span>
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadData"
          small
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { resolveImageSrc } from '@/utils/image'

const router = useRouter()
const communities = ref([])
const pageNum = ref(1)
const pageSize = ref(8)
const total = ref(0)

const loadData = async () => {
  const res = await request.get('/community/page', {
    params: { status: 1, pageNum: pageNum.value, pageSize: pageSize.value }
  })
  communities.value = res.data.list
  total.value = res.data.total
}

const goDetail = (id) => {
  router.push(`/community/${id}`)
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.community-list-page {
  min-height: calc(100vh - 140px);
  padding: 30px 0;
  background: #f5f7fa;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 20px;
  
  h2 {
    font-size: 20px;
    color: #333;
  }
}

.community-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.community-card {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  
  .card-image {
    width: 100%;
    height: 160px;
    overflow: hidden;
    cursor: pointer;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;
    }
    
    &:hover img {
      transform: scale(1.05);
    }
  }
  
  .card-content {
    padding: 15px;
    
    .name {
      font-size: 16px;
      color: #333;
      margin-bottom: 10px;
      font-weight: 600;
      cursor: pointer;
      transition: color 0.3s;
      
      &:hover {
        color: #409eff;
      }
    }
    
    .info-item {
      font-size: 13px;
      color: #666;
      margin-bottom: 8px;
      line-height: 1.5;
      
      .label {
        color: #999;
      }
    }
    
    .el-button {
      margin-top: 5px;
      background-color: #5cbdb9;
      border-color: #5cbdb9;
      
      &:hover {
        background-color: #4aa9a5;
        border-color: #4aa9a5;
      }
    }
  }
}

.pagination {
  margin-top: 30px;
  display: flex;
  align-items: center;
  gap: 15px;
  
  .total {
    font-size: 14px;
    color: #666;
  }
}

@media (max-width: 1200px) {
  .community-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .community-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
