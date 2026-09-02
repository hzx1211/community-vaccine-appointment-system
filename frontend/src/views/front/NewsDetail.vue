<template>
  <div class="news-detail-page">
    <div class="content-wrapper" v-if="news">
      <div class="breadcrumb">
        <router-link to="/news">社区资讯</router-link>
        <span class="separator">/</span>
        <span>资讯详情</span>
      </div>

      <div class="detail-card">
        <div class="article-header">
          <h1 class="title">{{ news.title }}</h1>
          <div class="meta">
            <span><el-icon><OfficeBuilding /></el-icon> {{ news.communityName || '未知社区' }}</span>
            <span><el-icon><View /></el-icon> {{ news.viewCount }} 浏览</span>
            <span><el-icon><Clock /></el-icon> {{ news.createTime?.substring(0, 16) }}</span>
          </div>
        </div>
        <div class="cover-image">
          <img :src="resolveImageSrc(news.coverImage, 'news')" />
        </div>
        <div class="article-summary" v-if="news.summary">
          <h3>简介</h3>
          <div class="summary-content" v-html="sanitizeRichHtml(news.summary)"></div>
        </div>
        <div class="article-content">
          <h3>正文</h3>
          <div class="content-body" v-html="sanitizeRichHtml(news.content, '暂无内容')"></div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <div class="section-header">
          <h3><el-icon><ChatDotRound /></el-icon> 评论区 ({{ totalComments }})</h3>
        </div>

        <!-- 发表评论 -->
        <div class="comment-form" v-if="isLoggedIn">
          <div class="user-avatar">
            <el-avatar :size="40" :src="userInfo.avatar || ''">{{ userInfo.username?.charAt(0) }}</el-avatar>
          </div>
          <div class="form-content">
            <el-input v-model="newComment" type="textarea" :rows="3" placeholder="发表你的看法..." maxlength="500" show-word-limit />
            <div class="form-actions">
              <el-button type="primary" @click="submitComment()" :loading="submitting">发表评论</el-button>
            </div>
          </div>
        </div>
        <div class="login-tip" v-else>
          <el-icon><Warning /></el-icon>
          <span>请先 <router-link to="/login">登录</router-link> 后发表评论</span>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <div v-if="commentTree.length === 0" class="empty-comment">
            <el-empty description="暂无评论，快来抢沙发吧~" />
          </div>
          <!-- 主评论 -->
          <div v-for="item in commentTree" :key="item.id" class="comment-item main-comment">
            <div class="comment-avatar">
              <el-avatar :size="40" :src="item.userAvatar || ''">{{ item.username?.charAt(0) }}</el-avatar>
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="username">{{ item.username }}</span>
                <span class="time">{{ formatTime(item.createTime) }}</span>
              </div>
              <div class="comment-content">{{ item.content }}</div>
              <div class="comment-actions">
                <span class="action-btn" v-if="isLoggedIn" @click="showReplyInput(item, item)">
                  <el-icon><ChatLineRound /></el-icon> 回复
                </span>
                <span class="action-btn delete" v-if="canDelete(item)" @click="deleteComment(item.id)">删除</span>
              </div>
              
              <!-- 子评论（回复） -->
              <div class="reply-list" v-if="item.children && item.children.length">
                <div v-for="reply in item.children" :key="reply.id" class="reply-item">
                  <div class="reply-avatar">
                    <el-avatar :size="28" :src="reply.userAvatar || ''">{{ reply.username?.charAt(0) }}</el-avatar>
                  </div>
                  <div class="reply-body">
                    <div class="reply-header">
                      <span class="username">{{ reply.username }}</span>
                      <span class="reply-to" v-if="reply.replyUsername">
                        回复 <span class="reply-name">@{{ reply.replyUsername }}</span>
                      </span>
                      <span class="time">{{ formatTime(reply.createTime) }}</span>
                    </div>
                    <div class="reply-content">{{ reply.content }}</div>
                    <div class="reply-actions">
                      <span class="action-btn" v-if="isLoggedIn" @click="showReplyInput(item, reply)">回复</span>
                      <span class="action-btn delete" v-if="canDelete(reply)" @click="deleteComment(reply.id)">删除</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 回复输入框 -->
              <div class="reply-form" v-if="replyingTo === item.id">
                <el-input v-model="replyContent" type="textarea" :rows="2" :placeholder="replyPlaceholder" maxlength="500" />
                <div class="reply-form-actions">
                  <el-button size="small" @click="cancelReply">取消</el-button>
                  <el-button type="primary" size="small" @click="submitReply" :loading="submitting">回复</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { resolveImageSrc } from '@/utils/image'
import { sanitizeRichHtml } from '@/utils/sanitize'

const route = useRoute()
const news = ref(null)
const comments = ref([])
const newComment = ref('')
const replyContent = ref('')
const replyingTo = ref(null)
const replyToComment = ref(null)
const replyPlaceholder = ref('')
const submitting = ref(false)

const userInfo = computed(() => JSON.parse(localStorage.getItem('userInfo') || '{}'))
const isLoggedIn = computed(() => !!localStorage.getItem('token') && userInfo.value.role === 'user')
const totalComments = computed(() => comments.value.length)

// 将评论列表转换为树形结构
const commentTree = computed(() => {
  const map = {}
  const roots = []
  
  // 先建立id到评论的映射
  comments.value.forEach(c => {
    map[c.id] = { ...c, children: [] }
  })
  
  // 构建树
  comments.value.forEach(c => {
    if (c.parentId && map[c.parentId]) {
      // 是回复，找到根评论
      let root = map[c.parentId]
      while (root.parentId && map[root.parentId]) {
        root = map[root.parentId]
      }
      root.children.push(map[c.id])
    } else if (!c.parentId) {
      roots.push(map[c.id])
    }
  })
  
  return roots
})

const loadData = async () => {
  const res = await request.get(`/news/${route.params.id}`)
  news.value = res.data
  loadComments()
}

const loadComments = async () => {
  const res = await request.get(`/comment/news/${route.params.id}`)
  comments.value = res.data || []
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    await request.post('/comment', { newsId: route.params.id, content: newComment.value.trim() })
    ElMessage.success('评论成功')
    newComment.value = ''
    loadComments()
  } finally {
    submitting.value = false
  }
}

const showReplyInput = (mainComment, targetComment) => {
  replyingTo.value = mainComment.id
  replyToComment.value = targetComment
  replyPlaceholder.value = `回复 @${targetComment.username}`
  replyContent.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
  replyToComment.value = null
  replyContent.value = ''
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  submitting.value = true
  try {
    await request.post('/comment', {
      newsId: route.params.id,
      content: replyContent.value.trim(),
      parentId: replyToComment.value.id
    })
    ElMessage.success('回复成功')
    cancelReply()
    loadComments()
  } finally {
    submitting.value = false
  }
}

const canDelete = (item) => userInfo.value.id === item.userId || userInfo.value.role === 'admin'

const deleteComment = async (id) => {
  await ElMessageBox.confirm('确定删除这条评论吗？', '提示', { type: 'warning' })
  await request.delete(`/comment/${id}`)
  ElMessage.success('删除成功')
  loadComments()
}

const formatTime = (time) => time ? time.substring(0, 16).replace('T', ' ') : ''

onMounted(loadData)
</script>

<style scoped lang="scss">
.news-detail-page {
  min-height: calc(100vh - 140px);
  padding: 30px 0;
  background: #f5f7fa;
}
.content-wrapper { max-width: 900px; margin: 0 auto; padding: 0 20px; }
.breadcrumb {
  margin-bottom: 20px; font-size: 14px; color: #666;
  a { color: #409eff; text-decoration: none; &:hover { text-decoration: underline; } }
  .separator { margin: 0 8px; color: #999; }
}
.detail-card { background: #fff; border-radius: 8px; padding: 30px; }
.article-header {
  text-align: center; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #eee;
  .title { font-size: 26px; color: #333; margin-bottom: 15px; line-height: 1.4; }
  .meta { display: flex; justify-content: center; gap: 30px; color: #999; font-size: 14px;
    span { display: flex; align-items: center; gap: 5px; }
  }
}
.cover-image { margin-bottom: 30px; text-align: center;
  img { max-width: 100%; max-height: 400px; border-radius: 8px; }
}
.article-summary {
  margin-bottom: 30px; padding: 20px; background: #f9f9f9; border-radius: 8px; border-left: 4px solid #409eff;
  h3 { font-size: 16px; color: #333; margin-bottom: 15px; }
  .summary-content { color: #666; line-height: 1.8; font-size: 15px; }
}
.article-content {
  h3 { font-size: 16px; color: #333; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 2px solid #409eff; display: inline-block; }
  .content-body { line-height: 1.8; color: #333; font-size: 15px; }
}
.comment-section { margin-top: 30px; background: #fff; border-radius: 8px; padding: 25px; }
.section-header { margin-bottom: 20px;
  h3 { font-size: 18px; color: #333; display: flex; align-items: center; gap: 8px; }
}
.comment-form {
  display: flex; gap: 15px; margin-bottom: 25px; padding-bottom: 25px; border-bottom: 1px solid #eee;
  .form-content { flex: 1; }
  .form-actions { margin-top: 10px; text-align: right; }
}
.login-tip {
  padding: 20px; background: #fef0f0; border-radius: 8px; color: #f56c6c;
  display: flex; align-items: center; gap: 8px; margin-bottom: 20px;
  a { color: #409eff; }
}

/* 评论列表样式 */
.comment-list {
  .main-comment {
    display: flex;
    gap: 12px;
    padding: 20px 0;
    border-bottom: 1px solid #f0f0f0;
    &:last-child { border-bottom: none; }
  }
  .comment-body { flex: 1; }
  .comment-header {
    display: flex; align-items: center; gap: 10px; margin-bottom: 8px;
    .username { font-weight: 600; color: #333; font-size: 14px; }
    .time { font-size: 12px; color: #999; }
  }
  .comment-content { color: #333; line-height: 1.6; font-size: 14px; margin-bottom: 10px; }
  .comment-actions {
    display: flex; gap: 16px;
    .action-btn {
      font-size: 12px; color: #909399; cursor: pointer; display: flex; align-items: center; gap: 4px;
      &:hover { color: #409eff; }
      &.delete:hover { color: #f56c6c; }
    }
  }
}

/* 回复列表样式 - 嵌套在主评论下方 */
.reply-list {
  margin-top: 15px;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  
  .reply-item {
    display: flex;
    gap: 10px;
    padding: 10px 0;
    border-bottom: 1px solid #eee;
    &:first-child { padding-top: 0; }
    &:last-child { border-bottom: none; padding-bottom: 0; }
  }
  .reply-body { flex: 1; }
  .reply-header {
    display: flex; align-items: center; gap: 8px; margin-bottom: 6px; flex-wrap: wrap;
    .username { font-weight: 600; color: #333; font-size: 13px; }
    .reply-to { font-size: 13px; color: #909399;
      .reply-name { color: #409eff; }
    }
    .time { font-size: 12px; color: #bbb; }
  }
  .reply-content { color: #333; line-height: 1.5; font-size: 13px; margin-bottom: 6px; }
  .reply-actions {
    display: flex; gap: 12px;
    .action-btn {
      font-size: 12px; color: #909399; cursor: pointer;
      &:hover { color: #409eff; }
      &.delete:hover { color: #f56c6c; }
    }
  }
}

/* 回复输入框 */
.reply-form {
  margin-top: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  .reply-form-actions {
    margin-top: 10px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
.empty-comment { padding: 40px 0; }
</style>
