<template>
  <div class="vaccine-manage-page">
    <div class="card">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchForm.name" placeholder="疫苗名称" style="width: 200px" clearable />
        <el-select v-model="searchForm.categoryId" placeholder="疫苗分类" style="width: 150px" clearable>
          <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button v-if="userStore.isAdmin" type="success" @click="handleAdd">新增疫苗</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="image" label="图片" width="80">
          <template #default="{ row }">
            <el-image v-if="row.image" :src="row.image" style="width: 50px; height: 50px" fit="cover" />
            <span v-else class="no-image">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="疫苗名称" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="manufacturer" label="生产厂家" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="appointmentCount" label="预约数" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="userStore.isAdmin" label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑疫苗' : '新增疫苗'" width="650px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="疫苗图片">
          <ImageUpload v-model="form.image" />
        </el-form-item>
        <el-form-item label="疫苗名称">
          <el-input v-model="form.name" placeholder="请输入疫苗名称" />
        </el-form-item>
        <el-form-item label="疫苗分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="生产厂家">
          <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接种剂次">
              <el-input-number v-model="form.doses" :min="1" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接种间隔">
              <el-input-number v-model="form.intervalDays" :min="0" style="width: 100%" />
              <span style="margin-left: 5px">天</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="适用人群">
          <el-input v-model="form.targetGroup" placeholder="请输入适用人群" />
        </el-form-item>
        <el-form-item label="禁忌症">
          <el-input v-model="form.contraindication" type="textarea" placeholder="请输入禁忌症" />
        </el-form-item>
        <el-form-item label="疫苗描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入疫苗描述" />
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
import { getVaccinePage, saveVaccine, deleteVaccine, updateVaccineStatus } from '@/api/vaccine'
import { getVaccineCategories } from '@/api/public'
import { ElMessage, ElMessageBox } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const tableData = ref([])
const categories = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const saving = ref(false)

const searchForm = reactive({ name: '', categoryId: null })
const form = reactive({
  id: null, name: '', categoryId: null, manufacturer: '', price: 0, stock: 0,
  doses: 1, intervalDays: 0, targetGroup: '', contraindication: '', description: '', image: ''
})

const loadCategories = async () => {
  const res = await getVaccineCategories()
  categories.value = res.data
}

const loadData = async () => {
  const res = await getVaccinePage({ ...searchForm, pageNum: pageNum.value, pageSize: pageSize.value })
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleAdd = () => {
  Object.assign(form, { id: null, name: '', categoryId: null, manufacturer: '', price: 0, stock: 0, doses: 1, intervalDays: 0, targetGroup: '', contraindication: '', description: '', image: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    await saveVaccine(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await updateVaccineStatus(row.id, newStatus)
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该疫苗？', '提示')
  await deleteVaccine(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped lang="scss">
.vaccine-manage-page {
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
