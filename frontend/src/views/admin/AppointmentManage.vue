<template>
  <div class="appointment-manage-page">
    <div class="card">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchForm.orderNo" placeholder="预约单号" style="width: 200px" clearable />
        <el-select v-model="searchForm.status" placeholder="状态" style="width: 120px" clearable>
          <!-- 系统管理员不显示待审核选项 -->
          <el-option v-if="!userStore.isAdmin" label="待审核" :value="0" />
          <el-option label="审核通过" :value="1" />
          <el-option label="审核拒绝" :value="2" />
          <el-option label="已支付" :value="3" />
          <el-option label="已接种" :value="4" />
          <el-option label="已取消" :value="5" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="orderNo" label="预约单号" width="180" />
        <el-table-column prop="realName" label="用户" width="100" />
        <el-table-column prop="vaccineName" label="疫苗" />
        <el-table-column prop="communityName" label="接种社区" />
        <el-table-column prop="appointmentDate" label="预约日期" width="110" />
        <el-table-column prop="timeSlot" label="时段" width="110" />
        <el-table-column prop="amount" label="金额" width="80">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status].type">{{ statusMap[row.status].text }}</el-tag>
          </template>
        </el-table-column>
        <!-- 操作列：系统管理员不显示任何操作按钮 -->
        <el-table-column v-if="!userStore.isAdmin" label="操作" width="180">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
              <el-button type="danger" size="small" @click="handleAudit(row, 2)">拒绝</el-button>
            </template>
            <el-button v-else-if="row.status === 3" type="primary" size="small" @click="handleVaccinate(row)">确认接种</el-button>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>

    <!-- 拒绝原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAppointmentPage, auditAppointment, confirmVaccinate } from '@/api/appointment'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentRow = ref(null)

const searchForm = reactive({ orderNo: '', status: null })

const statusMap = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '待支付', type: 'info' },
  2: { text: '已拒绝', type: 'danger' },
  3: { text: '已支付', type: 'primary' },
  4: { text: '已接种', type: 'success' },
  5: { text: '已取消', type: 'info' }
}

const loadData = async () => {
  const res = await getAppointmentPage({ ...searchForm, pageNum: pageNum.value, pageSize: pageSize.value })
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleAudit = async (row, status) => {
  if (status === 2) {
    currentRow.value = row
    rejectReason.value = ''
    rejectDialogVisible.value = true
  } else {
    await auditAppointment({ id: row.id, status })
    ElMessage.success('审核通过')
    loadData()
  }
}

const confirmReject = async () => {
  await auditAppointment({ id: currentRow.value.id, status: 2, rejectReason: rejectReason.value })
  ElMessage.success('已拒绝')
  rejectDialogVisible.value = false
  loadData()
}

const handleVaccinate = async (row) => {
  await ElMessageBox.confirm('确认该用户已完成接种？', '确认接种')
  await confirmVaccinate(row.id)
  ElMessage.success('已确认接种完成')
  loadData()
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.appointment-manage-page {
  .card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
  }
  
  .search-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .text-muted {
    color: #999;
  }
}
</style>
