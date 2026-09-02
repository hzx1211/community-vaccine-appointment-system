<template>
  <div class="news-manage-page">
    <div class="card">
      <div class="search-bar">
        <el-input v-model="searchForm.title" placeholder="标题" style="width: 200px" clearable />
        <el-select v-model="searchForm.status" placeholder="状态" style="width: 120px" clearable>
          <el-option label="待审核" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增资讯</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="coverImage" label="封面" width="80">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" style="width: 50px; height: 50px" fit="cover" />
            <span v-else class="no-image">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="communityName" label="所属社区" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status].type">{{ statusMap[row.status].text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">{{ row.createTime?.substring(0, 16) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <template v-if="row.status === 0">
              <el-button type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
              <el-button type="warning" size="small" @click="handleAudit(row, 2)">拒绝</el-button>
            </template>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="pageNum" :total="total" layout="total, prev, pager, next"
        @current-change="loadData" style="margin-top: 20px; justify-content: flex-end" />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑资讯' : '新增资讯'" width="850px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="封面图">
          <ImageUpload v-model="form.coverImage" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="所属社区">
          <el-select v-model="form.communityId" placeholder="请选择社区" style="width: 100%">
            <el-option v-for="item in communities" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介">
          <RichTextEditor v-model="form.summary" />
        </el-form-item>
        <el-form-item label="内容">
          <RichTextEditor v-model="form.content" />
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
import { getCommunities } from '@/api/public'
import { ElMessage, ElMessageBox } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import RichTextEditor from '@/components/RichTextEditor.vue'

const tableData = ref([])
const communities = ref([])
const pageNum = ref(1)
const total = ref(0)
const dialogVisible = ref(false)
const saving = ref(false)
const searchForm = reactive({ title: '', status: null })
const form = reactive({ id: null, title: '', summary: '', content: '', coverImage: '', communityId: null })
const statusMap = { 0: { text: '待审核', type: 'warning' }, 1: { text: '已发布', type: 'success' }, 2: { text: '已拒绝', type: 'danger' } }

const loadCommunities = async () => {
  const res = await getCommunities()
  communities.value = res.data
}

const loadData = async () => {
  const res = await request.get('/news/page', { params: { ...searchForm, pageNum: pageNum.value, pageSize: 10 } })
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleAdd = () => {
  Object.assign(form, { id: null, title: '', summary: '', content: '', coverImage: '', communityId: null })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    await request.post('/news', form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleAudit = async (row, status) => {
  await request.put(`/news/${row.id}/audit/${status}`)
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除？', '提示')
  await request.delete(`/news/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadCommunities()
  loadData()
})
</script>

<style scoped lang="scss">
.news-manage-page {
  .card { background: #fff; border-radius: 8px; padding: 20px; }
  .search-bar { display: flex; gap: 10px; margin-bottom: 20px; }
  .no-image { color: #999; font-size: 12px; }
}
</style>
