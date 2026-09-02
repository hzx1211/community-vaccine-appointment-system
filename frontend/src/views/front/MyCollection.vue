<template>
  <div class="my-collection-page">
    <div class="content-wrapper">
      <div class="page-header">
        <h2>我的关注</h2>
      </div>
      
      <div class="collection-grid" v-if="collections.length">
        <div v-for="item in collections" :key="item.id" class="collection-card">
          <img :src="resolveImageSrc(item.vaccineImage, 'vaccine')" class="vaccine-img" @click="router.push(`/vaccine/${item.vaccineId}`)" />
          <div class="info">
            <h3 class="name" @click="router.push(`/vaccine/${item.vaccineId}`)">{{ item.vaccineName }}</h3>
            <div class="bottom">
              <span class="price">¥{{ item.vaccinePrice }}</span>
              <el-button type="danger" size="small" @click="handleRemove(item)">取消关注</el-button>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-else description="暂无关注的疫苗" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { resolveImageSrc } from '@/utils/image'

const router = useRouter()
const collections = ref([])

const loadData = async () => {
  const res = await request.get('/collection/my')
  collections.value = res.data
}

const handleRemove = async (item) => {
  await request.delete(`/collection/${item.vaccineId}`)
  ElMessage.success('已取消关注')
  loadData()
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.my-collection-page {
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

.collection-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.collection-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  
  .vaccine-img {
    width: 100%;
    height: 150px;
    object-fit: cover;
    cursor: pointer;
    background: #f5f7fa;
  }
  
  .info {
    padding: 15px;
  }
  
  .name {
    font-size: 15px;
    color: #333;
    margin-bottom: 10px;
    cursor: pointer;
    
    &:hover {
      color: #409eff;
    }
  }
  
  .bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .price {
    color: #f56c6c;
    font-size: 18px;
    font-weight: bold;
  }
}
</style>
