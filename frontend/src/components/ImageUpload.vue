<template>
  <div class="image-upload">
    <el-upload
      class="uploader"
      :action="uploadUrl"
      :headers="headers"
      :show-file-list="false"
      :on-success="handleSuccess"
      :before-upload="beforeUpload"
      accept="image/*"
    >
      <img v-if="modelValue" :src="modelValue" class="preview" />
      <div v-else class="placeholder">
        <el-icon><Plus /></el-icon>
        <span>上传图片</span>
      </div>
    </el-upload>
    <div v-if="modelValue" class="actions">
      <el-button type="danger" size="small" @click="handleRemove">删除</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: String
})

const emit = defineEmits(['update:modelValue'])

const uploadUrl = '/api/file/upload'
const headers = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleSuccess = (res) => {
  if (res.code === 200) {
    emit('update:modelValue', res.data.url)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleRemove = () => {
  emit('update:modelValue', '')
}
</script>

<style scoped lang="scss">
.image-upload {
  .uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      overflow: hidden;
      transition: border-color 0.3s;
      
      &:hover {
        border-color: #409eff;
      }
    }
  }
  
  .preview {
    width: 150px;
    height: 150px;
    object-fit: cover;
    display: block;
  }
  
  .placeholder {
    width: 150px;
    height: 150px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8c939d;
    
    .el-icon {
      font-size: 28px;
      margin-bottom: 8px;
    }
    
    span {
      font-size: 12px;
    }
  }
  
  .actions {
    margin-top: 8px;
  }
}
</style>
