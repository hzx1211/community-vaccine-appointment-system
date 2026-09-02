<template>
  <div class="my-recharge-page">
    <div class="content-wrapper">
      <div class="page-header">
        <h2>我的充值</h2>
      </div>
      
      <!-- 余额卡片 -->
      <div class="balance-card">
        <div class="balance-info">
          <span class="label">账户余额</span>
          <span class="amount">¥{{ userStore.userInfo.balance || '0.00' }}</span>
        </div>
        <el-button type="primary" @click="dialogVisible = true">立即充值</el-button>
      </div>
      
      <!-- 充值记录 -->
      <div class="record-section">
        <h3>充值记录</h3>
        <el-table :data="records" style="width: 100%">
          <el-table-column prop="createTime" label="充值时间" width="180">
            <template #default="{ row }">{{ row.createTime?.substring(0, 19) }}</template>
          </el-table-column>
          <el-table-column prop="amount" label="充值金额" width="120">
            <template #default="{ row }">
              <span class="text-success">+¥{{ row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="payMethod" label="支付方式" width="120" />
          <el-table-column prop="beforeBalance" label="充值前余额" width="120">
            <template #default="{ row }">¥{{ row.beforeBalance }}</template>
          </el-table-column>
          <el-table-column prop="afterBalance" label="充值后余额" width="120">
            <template #default="{ row }">¥{{ row.afterBalance }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 充值弹窗 -->
    <el-dialog v-model="dialogVisible" title="账户充值" width="400px">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeForm.amount" :min="1" :max="10000" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="rechargeForm.payMethod">
            <el-radio label="支付宝">支付宝</el-radio>
            <el-radio label="微信">微信</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="loading">确认充值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const records = ref([])
const dialogVisible = ref(false)
const loading = ref(false)

const rechargeForm = reactive({
  amount: 100,
  payMethod: '支付宝'
})

const loadData = async () => {
  const res = await request.get('/recharge/my')
  records.value = res.data
}

const handleRecharge = async () => {
  loading.value = true
  try {
    await request.post('/recharge', rechargeForm)
    ElMessage.success('充值成功')
    dialogVisible.value = false
    loadData()
    userStore.fetchUserInfo()
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.my-recharge-page {
  min-height: calc(100vh - 140px);
  padding: 30px 0;
  background: #f5f7fa;
}

.content-wrapper {
  max-width: 1000px;
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

.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .balance-info {
    color: #fff;
    
    .label {
      display: block;
      font-size: 14px;
      opacity: 0.8;
      margin-bottom: 8px;
    }
    
    .amount {
      font-size: 36px;
      font-weight: bold;
    }
  }
}

.record-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  
  h3 {
    font-size: 16px;
    margin-bottom: 15px;
    color: #333;
  }
}

.text-success {
  color: #67c23a;
  font-weight: bold;
}
</style>
