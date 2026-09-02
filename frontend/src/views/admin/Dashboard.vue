<template>
  <div class="dashboard-page">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="icon" style="background: #409eff"><el-icon><Calendar /></el-icon></div>
        <div class="info">
          <span class="value">{{ stats.pendingCount || 0 }}</span>
          <span class="label">待审核预约</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background: #67c23a"><el-icon><Check /></el-icon></div>
        <div class="info">
          <span class="value">{{ stats.approvedCount || 0 }}</span>
          <span class="label">已通过预约</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background: #e6a23c"><el-icon><Wallet /></el-icon></div>
        <div class="info">
          <span class="value">{{ stats.paidCount || 0 }}</span>
          <span class="label">已支付预约</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background: #f56c6c"><el-icon><FirstAidKit /></el-icon></div>
        <div class="info">
          <span class="value">{{ stats.vaccinatedCount || 0 }}</span>
          <span class="label">已接种完成</span>
        </div>
      </div>
    </div>

    <!-- 公告列表 -->
    <div class="card">
      <div class="card-header">
        <h3><el-icon><Bell /></el-icon> 系统公告</h3>
      </div>
      <div class="announcement-list">
        <div v-for="item in announcements" :key="item.id" class="announcement-item" @click="showAnnouncement(item)">
          <el-icon><Bell /></el-icon>
          <span class="title">{{ item.title }}</span>
          <span class="date">{{ item.createTime?.substring(0, 10) }}</span>
        </div>
        <el-empty v-if="!announcements.length" description="暂无公告" />
      </div>
      <div class="list-pagination" v-if="announcementTotal > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="announcementTotal"
          layout="prev, pager, next"
          small
          @current-change="loadAnnouncements"
        />
      </div>
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="dialogVisible" :title="currentAnnouncement.title" width="600px">
      <div class="announcement-detail">
        <div class="detail-meta">
          <el-icon><Clock /></el-icon>
          <span>{{ currentAnnouncement.createTime?.substring(0, 16) }}</span>
        </div>
        <div class="detail-content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Bell, Clock } from '@element-plus/icons-vue'

const stats = ref({})
const announcements = ref([])
const pageNum = ref(1)
const pageSize = ref(8)
const announcementTotal = ref(0)
const dialogVisible = ref(false)
const currentAnnouncement = ref({})

const loadAnnouncements = async () => {
  const res = await request.get('/announcement/page', {
    params: { status: 1, pageNum: pageNum.value, pageSize: pageSize.value }
  })
  announcements.value = res.data.list
  announcementTotal.value = res.data.total
}

const showAnnouncement = (item) => {
  currentAnnouncement.value = item
  dialogVisible.value = true
}

onMounted(async () => {
  const statsRes = await request.get('/statistics/overview')
  stats.value = statsRes.data
  loadAnnouncements()
})
</script>

<style scoped lang="scss">
.dashboard-page {
  .stat-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 15px;
    
    .icon {
      width: 50px;
      height: 50px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 24px;
    }
    
    .info {
      .value {
        display: block;
        font-size: 28px;
        font-weight: bold;
        color: #333;
      }
      
      .label {
        font-size: 14px;
        color: #999;
      }
    }
  }
  
  .card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    
    .card-header {
      margin-bottom: 15px;
      
      h3 {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        color: #333;
        margin: 0;
        
        .el-icon {
          color: #e6a23c;
        }
      }
    }
  }
  
  .announcement-list {
    .announcement-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 12px 0;
      border-bottom: 1px dashed #eee;
      cursor: pointer;
      transition: all 0.3s;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background: #f9f9f9;
        margin: 0 -10px;
        padding: 12px 10px;
        border-radius: 6px;
        
        .title {
          color: #e6a23c;
        }
      }
      
      .el-icon {
        color: #e6a23c;
      }
      
      .title {
        flex: 1;
        color: #333;
        transition: color 0.3s;
      }
      
      .date {
        color: #999;
        font-size: 13px;
      }
    }
  }
  
  .list-pagination {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #eee;
    display: flex;
    justify-content: center;
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
    }
    
    .detail-content {
      color: #606266;
      font-size: 15px;
      line-height: 1.8;
      white-space: pre-wrap;
    }
  }
}

@media (max-width: 1200px) {
  .dashboard-page .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-page .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>
