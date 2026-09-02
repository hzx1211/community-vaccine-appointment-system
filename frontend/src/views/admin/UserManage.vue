<template>
  <div class="user-manage-page">
    <div class="card">
      <div class="search-bar">
        <el-input v-model="searchForm.username" placeholder="用户名" style="width: 150px" clearable />
        <el-input v-model="searchForm.realName" placeholder="真实姓名" style="width: 150px" clearable />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增{{ pageTitle }}</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="avatar" label="头像" width="70">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">{{ row.username?.charAt(0) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column v-if="currentRole === 'community_admin'" prop="communityName" label="所属社区" />
        <el-table-column v-if="currentRole === 'user'" prop="balance" label="余额" width="100">
          <template #default="{ row }">¥{{ row.balance || 0 }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">{{ row.createTime?.substring(0, 16) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button :type="row.status === 1 ? 'danger' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="pageNum" :total="total" layout="total, prev, pager, next"
        @current-change="loadData" style="margin-top: 20px; justify-content: flex-end" />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑' + pageTitle : '新增' + pageTitle" width="550px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="头像">
          <ImageUpload v-model="form.avatar" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item v-if="currentRole === 'community_admin'" label="所属社区">
          <el-select v-model="form.communityId" placeholder="请选择社区" style="width: 100%">
            <el-option v-for="item in communities" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isAdminRole" label="角色">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="系统管理员" value="admin" />
            <el-option label="社区管理员" value="community_admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'
import { getCommunities } from '@/api/public'
import { ElMessage, ElMessageBox } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'

const route = useRoute()
const tableData = ref([])
const communities = ref([])
const pageNum = ref(1)
const total = ref(0)
const dialogVisible = ref(false)
const searchForm = reactive({ username: '', realName: '' })
const form = reactive({ id: null, username: '', realName: '', phone: '', email: '', role: 'user', communityId: null, avatar: '' })

// 根据路由获取当前管理的用户角色
const currentRole = computed(() => route.meta.userRole || 'user')
const pageTitle = computed(() => {
  const titles = { admin: '管理员', community_admin: '社区管理员', user: '用户' }
  return titles[currentRole.value] || '用户'
})

// 判断是否是管理员角色（admin或community_admin）
const isAdminRole = computed(() => currentRole.value === 'admin' || currentRole.value === 'community_admin')

// 根据角色获取API路径
const getApiPath = () => isAdminRole.value ? '/admin' : '/user'

const loadCommunities = async () => {
  const res = await getCommunities()
  communities.value = res.data
}

const loadData = async () => {
  const apiPath = getApiPath()
  const params = { ...searchForm, pageNum: pageNum.value, pageSize: 10 }
  // 如果是管理员接口，需要传递role参数来区分admin和community_admin
  if (isAdminRole.value) {
    params.role = currentRole.value
  }
  const res = await request.get(`${apiPath}/page`, { params })
  tableData.value = res.data.list
  total.value = res.data.total
}

const handleAdd = () => {
  const defaultRole = isAdminRole.value ? currentRole.value : 'user'
  Object.assign(form, { id: null, username: '', realName: '', phone: '', email: '', role: defaultRole, communityId: null, avatar: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  const apiPath = getApiPath()
  await request.post(apiPath, form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleResetPwd = async (row) => {
  await ElMessageBox.confirm('确认重置密码为123456？', '提示')
  const apiPath = getApiPath()
  await request.post(`${apiPath}/${row.id}/reset-password`)
  ElMessage.success('密码已重置')
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const apiPath = getApiPath()
  await request.put(`${apiPath}/${row.id}/status/${newStatus}`)
  ElMessage.success('操作成功')
  loadData()
}

// 监听路由变化，重新加载数据
watch(() => route.meta.userRole, () => {
  pageNum.value = 1
  loadData()
})

onMounted(() => {
  loadData()
  loadCommunities()
})
</script>

<style scoped lang="scss">
.user-manage-page {
  .card { background: #fff; border-radius: 8px; padding: 20px; }
  .search-bar { display: flex; gap: 10px; margin-bottom: 20px; }
}
</style>
