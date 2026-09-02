<template>
  <div class="community-manage-page">
    <div class="card">
      <div class="search-bar">
        <el-input v-model="searchForm.name" placeholder="社区名称" style="width: 200px" clearable />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增社区</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedIds.length">
          批量删除 ({{ selectedIds.length }})
        </el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="image" label="图片" width="80">
          <template #default="{ row }">
            <el-image v-if="row.image" :src="row.image" style="width: 50px; height: 50px" fit="cover" />
            <span v-else class="no-image">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="社区名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column label="社区管理员" width="150">
          <template #default="{ row }">
            <span v-if="row.adminUsername">
              {{ row.adminRealName || row.adminUsername }}
            </span>
            <el-tag v-else type="info" size="small">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="pageNum" :total="total" layout="total, prev, pager, next"
        @current-change="loadData" style="margin-top: 20px; justify-content: flex-end" />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑社区' : '新增社区'" width="750px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="社区图片">
          <ImageUpload v-model="form.image" />
        </el-form-item>
        <el-form-item label="社区名称">
          <el-input v-model="form.name" placeholder="请输入社区名称" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="社区介绍">
          <RichTextEditor v-model="form.description" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import RichTextEditor from '@/components/RichTextEditor.vue'

const tableData = ref([])
const pageNum = ref(1)
const total = ref(0)
const dialogVisible = ref(false)
const saving = ref(false)
const selectedIds = ref([])
const searchForm = reactive({ name: '' })
const form = reactive({ id: null, name: '', address: '', contactPhone: '', description: '', image: '', status: 1 })

const loadData = async () => {
  const res = await request.get('/community/page', { params: { ...searchForm, pageNum: pageNum.value, pageSize: 10 } })
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  Object.assign(form, { id: null, name: '', address: '', contactPhone: '', description: '', image: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    await request.post('/community', form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除？', '提示')
  await request.delete(`/community/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 个社区？`, '批量删除')
  await request.delete('/community/batch', { data: selectedIds.value })
  ElMessage.success('批量删除成功')
  selectedIds.value = []
  loadData()
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.community-manage-page {
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
  
  .no-image {
    color: #999;
    font-size: 12px;
  }
}
</style>
