<template>
  <div class="vaccine-detail-page">
    <div class="content-wrapper" v-if="vaccine">
      <div class="detail-card">
        <div class="left">
          <img :src="resolveImageSrc(vaccine.image, 'vaccine')" class="vaccine-img" />
        </div>
        <div class="right">
          <h1 class="name">{{ vaccine.name }}</h1>
          <p class="category">{{ vaccine.categoryName }}</p>
          <div class="info-row">
            <span class="label">生产厂家：</span>
            <span>{{ vaccine.manufacturer }}</span>
          </div>
          <div class="info-row">
            <span class="label">接种剂次：</span>
            <span>{{ vaccine.doses }}剂</span>
          </div>
          <div class="info-row">
            <span class="label">接种间隔：</span>
            <span>{{ vaccine.intervalDays }}天</span>
          </div>
          <div class="info-row">
            <span class="label">适用人群：</span>
            <span>{{ vaccine.targetGroup || '无限制' }}</span>
          </div>
          <div class="info-row">
            <span class="label">库存数量：</span>
            <span :class="{ 'text-danger': vaccine.stock < 10 }">{{ vaccine.stock }}</span>
          </div>
          <div class="price-row">
            <span class="price">¥{{ vaccine.price }}</span>
            <span class="count">{{ vaccine.appointmentCount }}人已预约</span>
          </div>
          <div class="actions">
            <el-button type="primary" size="large" @click="handleAppointment" :disabled="vaccine.stock <= 0">
              {{ vaccine.stock > 0 ? '立即预约' : '暂无库存' }}
            </el-button>
            <el-button size="large" @click="handleCollection">
              <el-icon><Star /></el-icon>
              {{ isCollected ? '取消关注' : '关注' }}
            </el-button>
          </div>
        </div>
      </div>

      <div class="desc-card">
        <h2>疫苗详情</h2>
        <div class="content" v-html="sanitizeRichHtml(vaccine.description, '暂无详情')"></div>
        <h3 v-if="vaccine.contraindication">禁忌症</h3>
        <p v-if="vaccine.contraindication">{{ vaccine.contraindication }}</p>
      </div>
    </div>

    <!-- 预约弹窗 -->
    <el-dialog v-model="dialogVisible" title="预约接种" width="500px">
      <el-form :model="appointmentForm" label-width="100px">
        <el-form-item label="接种社区">
          <el-select v-model="appointmentForm.communityId" placeholder="请选择接种社区" style="width: 100%" @change="loadTimeSlotRemaining">
            <el-option v-for="item in communities" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="appointmentForm.appointmentDate" type="date" placeholder="选择日期" 
            :disabled-date="disabledDate" style="width: 100%" value-format="YYYY-MM-DD" @change="loadTimeSlotRemaining" />
        </el-form-item>
        <el-form-item label="预约时段">
          <el-select v-model="appointmentForm.timeSlot" placeholder="请选择时段" style="width: 100%" @change="loadTimeSlotRemaining">
            <el-option label="上午 09:00-12:00" value="09:00-12:00" />
            <el-option label="下午 14:00-17:00" value="14:00-17:00" />
          </el-select>
        </el-form-item>
        <el-form-item label="剩余容量" v-if="timeSlotRemaining !== null">
          <el-tag :type="timeSlotRemaining > 10 ? 'success' : timeSlotRemaining > 0 ? 'warning' : 'danger'">
            {{ timeSlotRemaining > 0 ? `剩余 ${timeSlotRemaining} 个名额` : '该时段已满' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="第几剂">
          <el-input-number v-model="appointmentForm.doseNumber" :min="1" :max="vaccine?.doses || 1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="appointmentForm.remark" type="textarea" placeholder="可填写特殊情况说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppointment" :loading="submitting">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router' //：useRoute（获取当前路由信息）、useRouter（路由跳转）
import { getVaccineById } from '@/api/vaccine'
import { getCommunities } from '@/api/public'
import { createAppointment } from '@/api/appointment'
import { useUserStore } from '@/stores/user' //状态管理：用户 store（管理登录状态和用户信息）
import { ElMessage } from 'element-plus'
import request from '@/utils/request' //HTTP 工具：封装的 axios 请求工具
import { resolveImageSrc } from '@/utils/image'
import { sanitizeRichHtml } from '@/utils/sanitize'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const vaccine = ref(null) //疫苗详情
const isCollected = ref(false)  //是否已收藏
const dialogVisible = ref(false)  // 控制预约对话框显示
const communities = ref([])  //社区列表
const submitting = ref(false)  // 控制提交按钮 loading
const timeSlotRemaining = ref(null)  // 时段剩余容量

const appointmentForm = reactive({
  vaccineId: null,
  communityId: null,
  appointmentDate: '',
  timeSlot: '',
  doseNumber: 1,
  remark: ''
})

const disabledDate = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

const loadData = async () => {
  const id = route.params.id
  const res = await getVaccineById(id)
  vaccine.value = res.data
  appointmentForm.vaccineId = id
  
  // 检查是否已收藏
  if (userStore.isLoggedIn) {
    try {
      const collectRes = await request.get(`/collection/check/${id}`)
      isCollected.value = collectRes.data
    } catch (e) {}
  }
}

const loadCommunities = async () => {
  const res = await getCommunities()
  communities.value = res.data
}

const handleAppointment = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 重置表单和容量显示
  timeSlotRemaining.value = null
  dialogVisible.value = true
}

// 加载时段剩余容量
const loadTimeSlotRemaining = async () => {
  if (!appointmentForm.communityId || !appointmentForm.appointmentDate || !appointmentForm.timeSlot) {
    timeSlotRemaining.value = null
    return
  }
  try {
    const res = await request.get('/appointment/time-slot/remaining', {
      params: {
        communityId: appointmentForm.communityId,
        date: appointmentForm.appointmentDate,
        timeSlot: appointmentForm.timeSlot
      }
    })
    timeSlotRemaining.value = res.data
  } catch (e) {
    timeSlotRemaining.value = null
  }
}

const submitAppointment = async () => {
  if (!appointmentForm.communityId || !appointmentForm.appointmentDate || !appointmentForm.timeSlot) {
    ElMessage.warning('请填写完整预约信息')
    return
  }
  // 检查时段是否已满
  if (timeSlotRemaining.value !== null && timeSlotRemaining.value <= 0) {
    ElMessage.warning('该时段已满，请选择其他时段')
    return
  }
  submitting.value = true
  try {
    await createAppointment(appointmentForm)
    ElMessage.success('预约成功，请等待审核')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleCollection = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    if (isCollected.value) {
      await request.delete(`/collection/${vaccine.value.id}`)
      isCollected.value = false
      ElMessage.success('已取消关注')
    } else {
      await request.post(`/collection/${vaccine.value.id}`)
      isCollected.value = true
      ElMessage.success('关注成功')
    }
  } catch (e) {}
}

onMounted(() => {
  loadData()
  loadCommunities()
})
</script>

<style scoped lang="scss">
.vaccine-detail-page {
  min-height: calc(100vh - 140px);
  padding: 30px 0;
  background: #f5f7fa;
}

.content-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.detail-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.left {
  flex-shrink: 0;
  
  .vaccine-img {
    width: 380px;
    max-height: 400px;
    object-fit: contain;
    border-radius: 12px;
    background: #f8f9fa;
    border: 1px solid #e4e7ed;
  }
}

.right {
  flex: 1;
  
  .name {
    font-size: 24px;
    color: #333;
    margin-bottom: 10px;
  }
  
  .category {
    display: inline-block;
    background: #ecf5ff;
    color: #409eff;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 14px;
    margin-bottom: 20px;
  }
  
  .info-row {
    margin-bottom: 12px;
    font-size: 14px;
    
    .label {
      color: #999;
    }
    
    .text-danger {
      color: #f56c6c;
    }
  }
  
  .price-row {
    margin: 20px 0;
    display: flex;
    align-items: baseline;
    gap: 20px;
    
    .price {
      font-size: 28px;
      color: #f56c6c;
      font-weight: bold;
    }
    
    .count {
      color: #999;
      font-size: 14px;
    }
  }
  
  .actions {
    display: flex;
    gap: 15px;
  }
}

.desc-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  
  h2 {
    font-size: 18px;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
  }
  
  h3 {
    font-size: 16px;
    margin: 20px 0 10px;
    color: #f56c6c;
  }
  
  .content {
    line-height: 1.8;
    color: #666;
    
    :deep(img) {
      max-width: 100%;
      height: auto;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .detail-card {
    flex-direction: column;
    padding: 20px;
  }
  
  .left {
    .vaccine-img {
      width: 100%;
      max-height: 300px;
    }
  }
  
  .right {
    .name {
      font-size: 20px;
    }
    
    .price-row .price {
      font-size: 24px;
    }
    
    .actions {
      flex-direction: column;
      
      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
