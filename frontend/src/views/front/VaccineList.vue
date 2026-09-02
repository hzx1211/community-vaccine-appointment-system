<template>
  <div class="vaccine-list-page">
    <div class="content-wrapper">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1><el-icon><FirstAidKit /></el-icon> 疫苗接种</h1>
        <p>选择适合您的疫苗，预约接种更便捷</p>
      </div>

      <!-- 搜索框 -->
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索疫苗名称、厂家..."
          size="large"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 分类筛选 -->
      <div class="filter-section">
        <div class="filter-title">
          <el-icon><Grid /></el-icon>
          <span>疫苗分类</span>
        </div>
        <div class="category-list">
          <div 
            class="category-item" 
            :class="{ active: categoryId === null }"
            @click="selectCategory(null)"
          >
            <div class="category-icon all">
              <el-icon><Menu /></el-icon>
            </div>
            <span>全部</span>
          </div>
          <div 
            v-for="(item, index) in categories" 
            :key="item.id"
            class="category-item"
            :class="{ active: categoryId === item.id }"
            @click="selectCategory(item.id)"
          >
            <div class="category-icon" :class="'color-' + (index % 6)">
              <el-icon><FirstAidKit /></el-icon>
            </div>
            <span>{{ item.name }}</span>
          </div>
        </div>
      </div>

      <!-- 疫苗列表 -->
      <div class="vaccine-section">
        <div class="section-header">
          <span class="result-count">共 <em>{{ total }}</em> 种疫苗</span>
        </div>
        <div class="vaccine-grid" v-if="vaccineList.length">
          <div v-for="item in vaccineList" :key="item.id" class="vaccine-card" @click="router.push(`/vaccine/${item.id}`)">
            <div class="card-image">
              <img :src="resolveImageSrc(item.image, 'vaccine')" />
              <div class="card-badge" v-if="item.price == 0">免费</div>
            </div>
            <div class="card-content">
              <div class="category-tag">{{ item.categoryName }}</div>
              <h3 class="name">{{ item.name }}</h3>
              <p class="manufacturer">
                <el-icon><OfficeBuilding /></el-icon>
                {{ item.manufacturer }}
              </p>
              <div class="meta-info">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ item.doses }}剂次
                </span>
                <span class="meta-item" :class="{ 'low-stock': item.stock < 50 }">
                  <el-icon><Box /></el-icon>
                  库存 {{ item.stock }}
                </span>
              </div>
              <div class="card-footer">
                <div class="price">
                  <span class="symbol">¥</span>
                  <span class="amount">{{ item.price }}</span>
                </div>
                <div class="appointment-count">
                  <el-icon><User /></el-icon>
                  {{ item.appointmentCount }}人已约
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无疫苗信息" />
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getVaccinePage } from '@/api/vaccine'
import { getVaccineCategories } from '@/api/public'
import { resolveImageSrc } from '@/utils/image'
import { FirstAidKit, Grid, Menu, OfficeBuilding, Calendar, Box, User, Search } from '@element-plus/icons-vue'

const router = useRouter()
const categories = ref([])
const vaccineList = ref([])
const categoryId = ref(null)
const searchKeyword = ref('')
const pageNum = ref(1)
const pageSize = ref(9)
const total = ref(0)

const loadCategories = async () => {
  const res = await getVaccineCategories()
  categories.value = res.data
}

const selectCategory = (id) => {
  categoryId.value = id
  pageNum.value = 1
  loadData()
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const loadData = async () => {
  const res = await getVaccinePage({
    name: searchKeyword.value || undefined,
    categoryId: categoryId.value,
    status: 1,
    pageNum: pageNum.value,
    pageSize: pageSize.value
  })
  vaccineList.value = res.data.list
  total.value = res.data.total
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped lang="scss">
.vaccine-list-page {
  min-height: calc(100vh - 140px);
  padding: 30px 0;
  background: var(--bg-color);
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 20px;
  
  h1 {
    font-size: 28px;
    color: #303133;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    margin-bottom: 10px;
    
    .el-icon {
      color: #409eff;
    }
  }
  
  p {
    color: #909399;
    font-size: 15px;
  }
}

.search-section {
  max-width: 600px;
  margin: 0 auto 24px;
  
  :deep(.el-input-group__append) {
    background-color: #409eff;
    border-color: #409eff;
    
    .el-button {
      color: #fff;
    }
  }
}

.filter-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  
  .el-icon {
    color: #409eff;
  }
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 90px;
  background: #f8f9fa;
  
  &:hover {
    background: #f0f7ff;
    transform: translateY(-2px);
  }
  
  &.active {
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    
    .category-icon {
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
    }
    
    span {
      color: #fff;
    }
  }
  
  span {
    font-size: 13px;
    color: #606266;
    font-weight: 500;
  }
}

.category-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  transition: all 0.3s ease;
  
  &.all { background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%); color: #fff; }
  &.color-0 { background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%); color: #fff; }
  &.color-1 { background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%); color: #fff; }
  &.color-2 { background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%); color: #fff; }
  &.color-3 { background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%); color: #fff; }
  &.color-4 { background: linear-gradient(135deg, #9b59b6 0%, #c39bd3 100%); color: #fff; }
  &.color-5 { background: linear-gradient(135deg, #1abc9c 0%, #48c9b0 100%); color: #fff; }
}

.vaccine-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.section-header {
  margin-bottom: 20px;
  
  .result-count {
    color: #909399;
    font-size: 14px;
    
    em {
      color: #409eff;
      font-style: normal;
      font-weight: 600;
    }
  }
}

.vaccine-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.vaccine-card {
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
  background: #fff;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    border-color: transparent;
    
    .card-image img {
      transform: scale(1.1);
    }
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
  
  .card-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    color: #fff;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
  }
}

.card-content {
  padding: 18px;
  
  .category-tag {
    display: inline-block;
    background: linear-gradient(135deg, #ecf5ff 0%, #e8f4ff 100%);
    color: #409eff;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    margin-bottom: 12px;
  }
  
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
    margin-bottom: 12px;
  }
  
  .meta-info {
    display: flex;
    gap: 16px;
    margin-bottom: 14px;
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #606266;
      
      &.low-stock {
        color: #f56c6c;
      }
    }
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
    .symbol { font-size: 14px; }
    .amount { font-size: 24px; font-weight: 700; }
  }
  
  .appointment-count {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #909399;
  }
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 1200px) {
  .vaccine-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .vaccine-grid { grid-template-columns: 1fr; }
  .category-list { gap: 8px; }
  .category-item { min-width: 70px; padding: 12px 14px; }
  .category-icon { width: 36px; height: 36px; font-size: 16px; }
}
</style>
