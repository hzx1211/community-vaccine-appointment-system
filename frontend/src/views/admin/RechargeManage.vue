<template>
  <div class="recharge-manage-page">
    <div class="card">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="amount" label="充值金额" width="120">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100" />
        <el-table-column prop="beforeBalance" label="充值前余额" width="120">
          <template #default="{ row }">¥{{ row.beforeBalance }}</template>
        </el-table-column>
        <el-table-column prop="afterBalance" label="充值后余额" width="120">
          <template #default="{ row }">¥{{ row.afterBalance }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="充值时间">
          <template #default="{ row }">{{ row.createTime?.substring(0, 19) }}</template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" :total="total" layout="total, prev, pager, next"
        @current-change="loadData" style="margin-top: 20px; justify-content: flex-end" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const tableData = ref([])
const pageNum = ref(1)
const total = ref(0)

const loadData = async () => {
  const res = await request.get('/recharge/page', { params: { pageNum: pageNum.value, pageSize: 10 } })
  tableData.value = res.data.list
  total.value = res.data.total
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.recharge-manage-page {
  .card { background: #fff; border-radius: 8px; padding: 20px; }
}
</style>
