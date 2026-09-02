<template>
  <div class="my-appointment-page">
    <div class="content-wrapper">
      <div class="page-header">
        <h2>我的预约</h2>
      </div>
      
      <div class="appointment-list">
        <el-table :data="appointments" style="width: 100%">
          <el-table-column prop="orderNo" label="预约单号" width="180" />
          <el-table-column prop="vaccineName" label="疫苗名称" />
          <el-table-column prop="communityName" label="接种社区" />
          <el-table-column prop="appointmentDate" label="预约日期" width="120" />
          <el-table-column prop="timeSlot" label="时段" width="120" />
          <el-table-column prop="amount" label="金额" width="100">
            <template #default="{ row }">¥{{ row.amount }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusMap[row.status].type">{{ statusMap[row.status].text }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 1" type="primary" size="small" @click="handlePay(row)">支付</el-button>
              <el-button v-if="row.status < 3" type="danger" size="small" @click="handleCancel(row)">取消</el-button>
              <span v-if="row.status >= 3" class="text-muted">-</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyAppointments, payAppointment, cancelAppointment } from '@/api/appointment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const appointments = ref([])

const statusMap = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '待支付', type: 'info' },
  2: { text: '已拒绝', type: 'danger' },
  3: { text: '已支付', type: 'primary' },
  4: { text: '已接种', type: 'success' },
  5: { text: '已取消', type: 'info' }
}

const loadData = async () => {
  const res = await getMyAppointments()
  appointments.value = res.data
}

const handlePay = async (row) => {
  await ElMessageBox.confirm(`确认支付 ¥${row.amount}？将从账户余额扣除`, '确认支付')
  await payAppointment(row.id)
  ElMessage.success('支付成功')
  loadData()
  // 更新用户余额
  userStore.fetchUserInfo()
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确认取消该预约？', '取消预约')
  await cancelAppointment(row.id)
  ElMessage.success('取消成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.my-appointment-page {
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

.appointment-list {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
</style>
