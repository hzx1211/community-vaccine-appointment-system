<template>
  <div class="my-profile-page">
    <div class="content-wrapper">
      <div class="page-header">
        <h2>个人信息</h2>
      </div>
      
      <div class="profile-card">
        <el-form :model="form" label-width="100px" style="max-width: 500px">
          <el-form-item label="头像">
            <div class="avatar-uploader">
              <el-upload
                class="avatar-upload"
                action="/api/file/upload"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-avatar :size="80" :src="form.avatar" v-if="form.avatar">
                  <template #default>
                    <el-icon><UserFilled /></el-icon>
                  </template>
                </el-avatar>
                <div class="avatar-placeholder" v-else>
                  <el-icon><Plus /></el-icon>
                </div>
                <div class="avatar-tip">点击更换头像</div>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
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
          <el-form-item label="身份证号">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSave" :loading="loading">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="password-card">
        <h3>修改密码</h3>
        <el-form :model="passwordForm" label-width="100px" style="max-width: 500px">
          <el-form-item label="原密码">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleChangePassword" :loading="pwdLoading">修改密码</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, updatePassword } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { Plus, UserFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const pwdLoading = ref(false)

const form = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  idCard: '',
  avatar: ''
})

const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    form.avatar = response.data.url
    ElMessage.success('头像上传成功')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

onMounted(() => {
  const info = userStore.userInfo
  form.username = info.username
  form.realName = info.realName
  form.phone = info.phone
  form.email = info.email
  form.idCard = info.idCard
  form.avatar = info.avatar
})

const handleSave = async () => {
  loading.value = true
  try {
    await updateProfile(form)
    ElMessage.success('保存成功')
    userStore.fetchUserInfo()
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  pwdLoading.value = true
  try {
    await updatePassword(passwordForm)
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.my-profile-page {
  min-height: calc(100vh - 140px);
  padding: 30px 0;
  background: #f5f7fa;
}

.content-wrapper {
  max-width: 800px;
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

.profile-card,
.password-card {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
}

.password-card {
  h3 {
    font-size: 16px;
    margin-bottom: 20px;
    color: #333;
  }
}

.avatar-uploader {
  .avatar-upload {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
  
  .avatar-placeholder {
    width: 80px;
    height: 80px;
    border: 1px dashed #d9d9d9;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    background: #fafafa;
    
    &:hover {
      border-color: #409eff;
    }
    
    .el-icon {
      font-size: 24px;
      color: #8c939d;
    }
  }
  
  .el-avatar {
    cursor: pointer;
    
    &:hover {
      opacity: 0.8;
    }
  }
  
  .avatar-tip {
    font-size: 12px;
    color: #999;
    margin-top: 8px;
  }
}
</style>
