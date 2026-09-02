<template>
  <div class="announcement-manage-page">
    <div class="card">
      <div class="search-bar">
        <el-button type="success" @click="handleAdd">新增公告</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">{{ row.createTime?.substring(0, 16) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const form = reactive({ id: null, title: '', content: '', sort: 0, status: 1 })

const loadData = async () => {
  const res = await request.get('/announcement/page', { params: { pageNum: 1, pageSize: 100 } })
  tableData.value = res.data.list
}

const handleAdd = () => {
  Object.assign(form, { id: null, title: '', content: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => { Object.assign(form, row); dialogVisible.value = true }

const handleSave = async () => {
  await request.post('/announcement', form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除？', '提示')
  await request.delete(`/announcement/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.announcement-manage-page {
  .card { background: #fff; border-radius: 8px; padding: 20px; }
  .search-bar { margin-bottom: 20px; }
}
</style>
