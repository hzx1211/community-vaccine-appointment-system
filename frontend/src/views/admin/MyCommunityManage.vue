<template>
  <div class="my-community-page">
    <div class="card" v-loading="loading">
      <div class="page-header">
        <h2>我的社区</h2>
        <p class="subtitle">查看您负责的社区信息</p>
      </div>

      <template v-if="community">
        <el-descriptions :column="2" border class="community-info">
          <el-descriptions-item label="社区名称">{{ community.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ community.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="社区地址" :span="2">{{ community.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="社区图片" :span="2">
            <el-image 
              v-if="community.image" 
              :src="community.image" 
              :preview-src-list="[community.image]"
              style="width: 150px; height: 100px; border-radius: 4px;"
              fit="cover"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="社区介绍" :span="2">
            <div v-if="community.description" v-html="community.description" class="rich-text-content"></div>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 社区统计信息 -->
        <el-divider content-position="left">社区统计</el-divider>
        <el-row :gutter="20" class="stats-row">
          <el-col :span="8">
            <div class="stat-card">
              <div class="stat-value">{{ stats.totalAppointments || 0 }}</div>
              <div class="stat-label">总预约数</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card">
              <div class="stat-value">{{ stats.pendingAppointments || 0 }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card">
              <div class="stat-value">{{ stats.completedAppointments || 0 }}</div>
              <div class="stat-label">已接种</div>
            </div>
          </el-col>
        </el-row>
      </template>

      <el-empty v-else description="您还未被分配管理社区" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const community = ref(null)
const stats = ref({})

const loadMyCommunity = async () => {
  loading.value = true
  try {
    const res = await request.get('/community/my')
    if (res.data) {
      community.value = res.data
      loadStats()
    }
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const res = await request.get('/community/my/stats')
    stats.value = res.data || {}
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

onMounted(loadMyCommunity)
</script>

<style scoped lang="scss">
.my-community-page {
  .card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
  }

  .page-header {
    margin-bottom: 24px;
    
    h2 {
      margin: 0 0 8px;
      font-size: 20px;
      color: #303133;
    }
    
    .subtitle {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }

  .community-info {
    max-width: 800px;
    margin-bottom: 24px;

    .rich-text-content {
      :deep(p) {
        margin: 0 0 8px;
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .stats-row {
    margin-top: 20px;
  }

  .stat-card {
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
    border-radius: 8px;
    padding: 20px;
    text-align: center;

    .stat-value {
      font-size: 28px;
      font-weight: 600;
      color: #409eff;
    }

    .stat-label {
      margin-top: 8px;
      font-size: 14px;
      color: #606266;
    }
  }
}
</style>
