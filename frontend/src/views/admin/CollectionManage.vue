<template>
  <div class="collection-manage-page">
    <div class="card">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="vaccineName" label="疫苗名称" />
        <el-table-column prop="vaccinePrice" label="疫苗价格" width="120">
          <template #default="{ row }">¥{{ row.vaccinePrice }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="收藏时间">
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
  const res = await request.get('/collection/page', { params: { pageNum: pageNum.value, pageSize: 10 } })
  tableData.value = res.data.list
  total.value = res.data.total
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.collection-manage-page {
  .card { background: #fff; border-radius: 8px; padding: 20px; }
}
</style>
