<template>
  <div class="film-list">
    <!-- 页面头部 全站统一毛玻璃header -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">
            <span class="title-icon">💬</span>
            影院留言管理
          </div>
          <div class="page-subtitle">查看用户反馈留言，在线回复客户问题</div>
        </div>
        <!-- 装饰元素 -->
        <div class="header-decoration">
          <div class="deco-circle deco-circle--1"></div>
          <div class="deco-circle deco-circle--2"></div>
          <div class="deco-circle deco-circle--3"></div>
        </div>
      </div>
    </div>

    <!-- 统计卡片行 -->
    <div class="stats-row" v-if="messageList.length > 0 || isLoaded">
      <div class="stat-card stat-card--total">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ totalCount }}</span>
          <span class="stat-label">留言总数</span>
        </div>
      </div>
      <div class="stat-card stat-card--replied">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round">
            <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14z" />
            <rect x="2" y="9" width="4" height="11" rx="1" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ repliedCount }}</span>
          <span class="stat-label">已回复</span>
        </div>
      </div>
      <div class="stat-card stat-card--pending">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ pendingCount }}</span>
          <span class="stat-label">待回复</span>
        </div>
      </div>
    </div>

    <!-- 留言列表主卡片 -->
    <div class="table-card" v-loading="loading" element-loading-text="正在加载留言数据...">
      <!-- 空状态 -->
      <div v-if="isLoaded && paginatedList.length === 0" class="empty-state">
        <div class="empty-illustration">
          <svg viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="20" y="30" width="80" height="60" rx="12" fill="#f1f5f9" stroke="#e2e8f0" stroke-width="2" />
            <line x1="35" y1="48" x2="85" y2="48" stroke="#cbd5e1" stroke-width="3" stroke-linecap="round" />
            <line x1="35" y1="60" x2="70" y2="60" stroke="#e2e8f0" stroke-width="3" stroke-linecap="round" />
            <line x1="35" y1="72" x2="60" y2="72" stroke="#e2e8f0" stroke-width="3" stroke-linecap="round" />
            <circle cx="95" cy="38" r="18" fill="#f8fafc" stroke="#e2e8f0" stroke-width="2" />
            <text x="95" y="43" text-anchor="middle" font-size="18" fill="#94a3b8">✉</text>
          </svg>
        </div>
        <p class="empty-title">暂无用户留言反馈</p>
        <p class="empty-desc">当有用户提交留言时，会在这里显示</p>
      </div>

      <!-- 留言列表 -->
      <div v-else-if="paginatedList.length > 0" class="message-list">
        <TransitionGroup name="message-card-anim">
          <div v-for="(item, idx) in paginatedList" :key="item.leavingMessage.id || idx" class="message-card"
            :class="{ 'message-card--replied': item.leavingMessage.reply }">
            <!-- 状态角标 -->
            <div class="message-status-badge" v-if="item.leavingMessage.reply">
              <span class="badge-dot"></span>
              已回复
            </div>
            <div class="message-status-badge message-status-badge--pending" v-else>
              <span class="badge-dot badge-dot--pending"></span>
              待回复
            </div>

            <!-- 用户头像昵称区域 -->
            <div class="message-header">
              <div class="avatar-wrapper">
                <img class="user-avatar" :src="item.user.avatar" alt="用户头像" @error="onAvatarError" />
                <div class="avatar-ring"></div>
              </div>
              <div class="user-info">
                <div class="user-name">{{ item.user.nickname || '匿名访客' }}</div>
                <div class="message-time">
                  <svg class="time-icon" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="8" r="6.5" stroke="currentColor" stroke-width="1.2" />
                    <path d="M8 4.5V8l2.5 1.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"
                      stroke-linejoin="round" />
                  </svg>
                  {{ formatTime(item.leavingMessage.createAt) }}
                </div>
              </div>
            </div>

            <!-- 用户留言内容 -->
            <div class="message-content">
              <div class="content-label">
                <svg class="label-icon" viewBox="0 0 16 16" fill="currentColor">
                  <path
                    d="M3 3.5a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9a.5.5 0 0 1-.5-.5zm0 3a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9a.5.5 0 0 1-.5-.5zm0 3a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5z" />
                </svg>
                用户留言
              </div>
              <div class="content-text">{{ item.leavingMessage.content }}</div>
            </div>

            <!-- 已有回复展示 -->
            <div class="message-reply" v-if="item.leavingMessage.reply">
              <div class="reply-header">
                <div class="reply-label">
                  <svg class="label-icon" viewBox="0 0 16 16" fill="currentColor">
                    <path
                      d="M2.5 1A1.5 1.5 0 0 0 1 2.5v7A1.5 1.5 0 0 0 2.5 11H6v2.5a.5.5 0 0 0 .854.354l3-3a.5.5 0 0 0-.354-.854H6v-1h7.5a1.5 1.5 0 0 0 1.5-1.5v-7A1.5 1.5 0 0 0 13.5 1h-11z" />
                  </svg>
                  客服回复
                </div>
                <span class="reply-badge">官方回复</span>
              </div>
              <div class="reply-text">{{ item.leavingMessage.reply }}</div>
            </div>

            <!-- 操作按钮 -->
            <div class="message-actions">
              <el-button size="default" :type="item.leavingMessage.reply ? 'default' : 'primary'"
                :class="item.leavingMessage.reply ? 'btn-re reply-again-btn' : 'btn-reply'" round
                @click="handleReply(item, idx)">
                <el-icon class="btn-icon">
                  <ChatDotRound />
                </el-icon>
                {{ item.leavingMessage.reply ? '重新回复' : '立即回复' }}
              </el-button>
            </div>
          </div>
        </TransitionGroup>
      </div>

      <!-- 分页底部 -->
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" background />
      </div>
    </div>

    <!-- 回复弹窗 全站统一dialog样式 -->
    <el-dialog title="回复用户留言" v-model="dialogFormVisible" width="46%" align-center class="film-dialog film-dialog--edit"
      :close-on-click-modal="false" destroy-on-close>
      <!-- 弹窗内预览：用户原留言 -->
      <div class="dialog-preview" v-if="currentReplyItem">
        <div class="preview-header">
          <img class="preview-avatar" :src="currentReplyItem.user?.avatar" alt="" @error="onAvatarError" />
          <div class="preview-user">
            <span class="preview-name">{{ currentReplyItem.user?.nickname || '匿名访客' }}</span>
            <span class="preview-time">{{ formatTime(currentReplyItem.leavingMessage?.createAt) }}</span>
          </div>
        </div>
        <div class="preview-content">
          {{ currentReplyItem.leavingMessage?.content }}
        </div>
      </div>

      <el-form :model="form" label-width="85px" class="film-dialog__form">
        <el-form-item label="回复内容">
          <el-input v-model="form.reply" type="textarea" :rows="6" placeholder="请输入贴心回复内容，礼貌解答用户疑问" maxlength="1000"
            show-word-limit class="reply-textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogFormVisible = false" class="btn-cancel">取消</el-button>
          <el-button size="large" type="primary" @click="submitReply" class="btn-submit" :loading="submitting">
            <el-icon class="btn-icon">
              <Check />
            </el-icon>
            确认提交回复
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Check } from '@element-plus/icons-vue'
import { ListAllLeavingMessage, ReplyLeavingMessage } from '@/api/leaving'

// ==================== 响应式状态 ====================
const loading = ref(false)
const isLoaded = ref(false)
const submitting = ref(false)
const messageList = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const dialogFormVisible = ref(false)
const currentReplyItem = ref(null)

const form = reactive({
  id: null,
  reply: ''
})

// ==================== 计算属性 ====================
const totalCount = computed(() => messageList.value.length)

const repliedCount = computed(() =>
  messageList.value.filter(item => item.leavingMessage?.reply).length
)

const pendingCount = computed(() => totalCount.value - repliedCount.value)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return messageList.value.slice(start, start + pageSize.value)
})

// ==================== 方法 ====================
function loadMessages() {
  loading.value = true
  ListAllLeavingMessage()
    .then(res => {
      if (!res?.success) return
      messageList.value = res.data || []
      currentPage.value = 1
      isLoaded.value = true
    })
    .catch(err => {
      console.error('加载留言失败', err)
      ElMessage.error('加载留言数据失败，请稍后重试')
    })
    .finally(() => {
      loading.value = false
    })
}

function handleSizeChange(newSize) {
  pageSize.value = newSize
  currentPage.value = 1
}

function handleCurrentChange(newPage) {
  currentPage.value = newPage
}

function handleReply(item) {
  currentReplyItem.value = item
  form.id = item.leavingMessage.id
  form.reply = item.leavingMessage.reply || ''
  dialogFormVisible.value = true
}

function submitReply() {
  const trimReply = form.reply.trim()
  if (!trimReply) {
    ElMessage.warning('请填写回复内容，不能为空')
    return
  }
  if (trimReply.length < 2) {
    ElMessage.warning('回复内容至少需要2个字符')
    return
  }
  submitting.value = true
  ReplyLeavingMessage({ id: form.id, reply: trimReply })
    .then(res => {
      if (!res?.success) return
      ElMessage.success('回复提交成功，用户将看到您的暖心回复 ✨')
      dialogFormVisible.value = false
      currentReplyItem.value = null
      loadMessages()
    })
    .catch(err => {
      console.error('回复提交失败', err)
      ElMessage.error('回复提交失败，请稍后重试')
    })
    .finally(() => {
      submitting.value = false
    })
}

function formatTime(timeStr) {
  if (!timeStr) return '未知时间'
  try {
    // 尝试格式化，兼容多种时间格式
    const date = new Date(timeStr)
    if (isNaN(date.getTime())) return timeStr
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch {
    return timeStr
  }
}

function onAvatarError(e) {
  // 头像加载失败时使用默认占位图
  e.target.src = 'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52" fill="#e2e8f0"><circle cx="26" cy="26" r="26" fill="#f1f5f9"/><circle cx="26" cy="22" r="10" fill="#cbd5e1"/><ellipse cx="26" cy="48" rx="18" ry="12" fill="#cbd5e1"/></svg>'
  )
}

// ==================== 生命周期 ====================
onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
/* ==================== 全局页面容器 ==================== */
.film-list {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 24px;
  box-sizing: border-box;
  background:
    radial-gradient(ellipse at 20% 20%, rgba(59, 130, 246, 0.03) 0%, transparent 60%),
    radial-gradient(ellipse at 80% 70%, rgba(99, 102, 241, 0.03) 0%, transparent 60%),
    radial-gradient(ellipse at 50% 50%, rgba(168, 85, 247, 0.02) 0%, transparent 70%),
    #f8fafc;
}

/* ==================== 顶部头部 ==================== */
.page-header {
  flex: 0 0 auto;
  padding: 22px 28px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow:
    0 4px 24px rgba(15, 23, 42, 0.04),
    0 1px 4px rgba(15, 23, 42, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.75);
  position: relative;
  overflow: hidden;
}

.page-search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.page-search-bar__title {
  min-width: 240px;
}

.page-title {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.3px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 28px;
  display: inline-block;
  animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-4px);
  }
}

.page-subtitle {
  margin-top: 7px;
  font-size: 14px;
  color: #64748b;
  font-weight: 450;
  letter-spacing: 0.1px;
}

/* 头部装饰 */
.header-decoration {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-shrink: 0;
}

.deco-circle {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  opacity: 0.5;
  animation: decoPulse 2.5s ease-in-out infinite;
}

.deco-circle--1 {
  background: #3b82f6;
  animation-delay: 0s;
  width: 8px;
  height: 8px;
}

.deco-circle--2 {
  background: #6366f1;
  animation-delay: 0.6s;
  width: 12px;
  height: 12px;
}

.deco-circle--3 {
  background: #8b5cf6;
  animation-delay: 1.2s;
  width: 9px;
  height: 9px;
}

@keyframes decoPulse {

  0%,
  100% {
    transform: scale(1);
    opacity: 0.45;
  }

  50% {
    transform: scale(1.7);
    opacity: 0.85;
  }
}

/* ==================== 统计卡片行 ==================== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  flex: 0 0 auto;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 22px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow:
    0 2px 12px rgba(15, 23, 42, 0.03),
    0 1px 3px rgba(15, 23, 42, 0.02);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow:
    0 8px 24px rgba(15, 23, 42, 0.06),
    0 2px 6px rgba(15, 23, 42, 0.03);
}

.stat-icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 22px;
  height: 22px;
}

.stat-card--total .stat-icon {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  color: #3b82f6;
}

.stat-card--replied .stat-icon {
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  color: #10b981;
}

.stat-card--pending .stat-icon {
  background: linear-gradient(135deg, #fffbeb, #fef3c7);
  color: #f59e0b;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1;
  letter-spacing: -1px;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

/* ==================== 外层主卡片容器 ==================== */
.table-card {
  flex: 1;
  min-height: 0;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 22px 24px 20px;
  box-shadow:
    0 4px 24px rgba(15, 23, 42, 0.04),
    0 1px 4px rgba(15, 23, 42, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.75);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

/* ==================== 空状态 ==================== */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
}

.empty-illustration {
  margin-bottom: 20px;
  opacity: 0.75;
}

.empty-illustration svg {
  width: 140px;
  height: 140px;
}

.empty-title {
  font-size: 18px;
  font-weight: 700;
  color: #334155;
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

/* ==================== 留言滚动列表 ==================== */
.message-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-right: 6px;
}

/* ==================== 单条留言卡片 ==================== */
.message-card {
  position: relative;
  background: #ffffff;
  border: 1.5px solid #eef2f7;
  border-radius: 20px;
  padding: 22px 26px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
}

.message-card:hover {
  transform: translateY(-2px);
  border-color: #e2e8f0;
  box-shadow:
    0 12px 32px rgba(15, 23, 42, 0.07),
    0 2px 8px rgba(15, 23, 42, 0.03);
}

.message-card--replied {
  border-left: 4px solid #10b981;
  background: linear-gradient(135deg, #ffffff 0%, #f9fefb 100%);
}

.message-card:not(.message-card--replied) {
  border-left: 4px solid #f59e0b;
  background: linear-gradient(135deg, #ffffff 0%, #fffdf8 100%);
}

/* 状态角标 */
.message-status-badge {
  position: absolute;
  top: 16px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  padding: 5px 12px;
  border-radius: 999px;
  background: #ecfdf5;
  color: #059669;
  letter-spacing: 0.2px;
}

.message-status-badge--pending {
  background: #fffbeb;
  color: #d97706;
}

.badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #10b981;
  animation: dotPulse 2s ease-in-out infinite;
}

.badge-dot--pending {
  background: #f59e0b;
  animation: dotPulse 1.5s ease-in-out infinite;
}

@keyframes dotPulse {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }
}

/* 头部头像信息 */
.message-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 18px;
  padding-right: 70px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
  display: block;
}

.avatar-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.55;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-time {
  font-size: 13px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  gap: 5px;
}

.time-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

/* 用户留言内容块 */
.message-content {
  margin-bottom: 16px;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 14px;
  border: 1px solid #f1f5f9;
  transition: background 0.25s ease;
}

.message-card:hover .message-content {
  background: #f1f5f9;
}

.content-label {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  letter-spacing: 0.2px;
}

.label-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  opacity: 0.7;
}

.content-text {
  font-size: 15px;
  line-height: 1.75;
  color: #1e293b;
  word-break: break-word;
  white-space: pre-wrap;
}

/* 客服回复区块 */
.message-reply {
  background: linear-gradient(135deg, #f0f7ff 0%, #f9fbff 100%);
  border: 1.5px solid #dbeafe;
  border-radius: 16px;
  padding: 16px 20px;
  margin-bottom: 18px;
  position: relative;
}

.message-reply::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 24px;
  width: 16px;
  height: 16px;
  background: #f0f7ff;
  border-left: 1.5px solid #dbeafe;
  border-top: 1.5px solid #dbeafe;
  transform: rotate(45deg);
  border-radius: 3px 0 0 0;
}

.reply-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.reply-label {
  font-size: 13px;
  font-weight: 600;
  color: #3b82f6;
  display: flex;
  align-items: center;
  gap: 6px;
  letter-spacing: 0.2px;
}

.reply-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 999px;
  background: #dbeafe;
  color: #2563eb;
  letter-spacing: 0.3px;
}

.reply-text {
  font-size: 15px;
  line-height: 1.75;
  color: #1e293b;
  word-break: break-word;
  white-space: pre-wrap;
}

/* 操作按钮区域 */
.message-actions {
  display: flex;
  justify-content: flex-end;
}

.btn-reply,
.btn-re {
  border-radius: 999px !important;
  min-width: 120px;
  height: 38px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  letter-spacing: 0.2px;
}

.btn-reply {
  background: linear-gradient(135deg, #3b82f6, #2563eb) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.3);
}

.btn-reply:hover {
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.45);
  transform: translateY(-1px);
}

.btn-reply:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}

.reply-again-btn {
  background: #fff !important;
  border: 1.5px solid #e2e8f0 !important;
  color: #475569 !important;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.reply-again-btn:hover {
  border-color: #3b82f6 !important;
  color: #3b82f6 !important;
  background: #f8faff !important;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.12);
}

.btn-icon {
  margin-right: 5px;
  font-size: 15px;
}

/* 列表过渡动画 */
.message-card-anim-enter-active {
  transition: all 0.45s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-card-anim-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-card-anim-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.message-card-anim-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.message-card-anim-move {
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ==================== 分页容器 ==================== */
.pagination-container {
  flex-shrink: 0;
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid #e8ecf1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination-container :deep(.el-pagination) {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  --el-pagination-bg-color: #f8fafc;
  --el-pagination-hover-color: #3b82f6;
}

.pagination-container :deep(.el-pager li) {
  border-radius: 10px !important;
  font-weight: 600;
  transition: all 0.25s ease;
  min-width: 36px;
  height: 36px;
  line-height: 36px;
  border: 1px solid transparent;
}

.pagination-container :deep(.el-pager li:hover) {
  background: #eff6ff;
  border-color: #dbeafe;
  color: #3b82f6;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #3b82f6, #6366f1) !important;
  color: #fff !important;
  border: none;
  box-shadow: 0 3px 10px rgba(59, 130, 246, 0.35);
  font-weight: 700;
}

.pagination-container :deep(.btn-prev),
.pagination-container :deep(.btn-next) {
  border-radius: 10px !important;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  transition: all 0.25s ease;
}

.pagination-container :deep(.btn-prev:hover),
.pagination-container :deep(.btn-next:hover) {
  background: #eff6ff;
  border-color: #3b82f6;
  color: #3b82f6;
}

.pagination-container :deep(.el-select .el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

/* ==================== 弹窗统一样式 ==================== */
.film-dialog {
  border-radius: 22px !important;
  overflow: hidden;
}

.film-dialog :deep(.el-dialog__header) {
  padding: 22px 28px 18px;
  background: linear-gradient(135deg, #fafcfd, #ffffff);
  border-bottom: 1px solid #eef2f7;
  position: relative;
}

.film-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.2px;
}

.film-dialog :deep(.el-dialog__body) {
  padding: 22px 28px 12px;
  background: #fbfdff;
}

.film-dialog :deep(.el-dialog__footer) {
  padding: 14px 28px 22px;
  background: #fbfdff;
  border-top: 1px solid #eef2f7;
}

.film-dialog :deep(.el-dialog__close) {
  font-size: 20px;
  color: #94a3b8;
  transition: all 0.25s ease;
}

.film-dialog :deep(.el-dialog__close:hover) {
  color: #0f172a;
  transform: rotate(90deg);
}

/* 弹窗内预览区 */
.dialog-preview {
  background: #f8fafc;
  border: 1px solid #eef2f7;
  border-radius: 14px;
  padding: 16px 18px;
  margin-bottom: 20px;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.preview-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.preview-user {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.preview-name {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
}

.preview-time {
  font-size: 12px;
  color: #94a3b8;
}

.preview-content {
  font-size: 14px;
  line-height: 1.65;
  color: #475569;
  padding: 10px 14px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #f1f5f9;
  word-break: break-word;
  white-space: pre-wrap;
}

/* 弹窗表单 */
.film-dialog__form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
  font-size: 14px;
}

.reply-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 1.5px solid #e2e8f0;
  background: #fafcfd;
  transition: all 0.3s ease;
  font-size: 14px;
  line-height: 1.7;
  resize: vertical;
  padding: 14px 16px;
}

.reply-textarea :deep(.el-textarea__inner):focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
  background: #fff;
}

.reply-textarea :deep(.el-textarea__inner):hover {
  border-color: #94a3b8;
}

/* 弹窗底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  min-width: 110px;
  height: 44px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.2px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-cancel {
  background: #f8fafc !important;
  border: 1.5px solid #e2e8f0 !important;
  color: #64748b !important;
}

.btn-cancel:hover {
  background: #f1f5f9 !important;
  border-color: #cbd5e1 !important;
  color: #334155 !important;
}

.btn-submit {
  background: linear-gradient(135deg, #3b82f6, #6366f1) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.btn-submit:hover {
  box-shadow: 0 6px 22px rgba(59, 130, 246, 0.45);
  transform: translateY(-1px);
}

.btn-submit:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}

/* ==================== 加载状态优化 ==================== */
.table-card :deep(.el-loading-mask) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(4px);
}

.table-card :deep(.el-loading-spinner .el-loading-text) {
  color: #64748b;
  font-size: 14px;
}

/* ==================== 自定义滚动条 ==================== */
.message-list::-webkit-scrollbar {
  width: 5px;
}

.message-list::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 10px;
  margin: 4px 0;
}

.message-list::-webkit-scrollbar-thumb {
  background: #dce3eb;
  border-radius: 10px;
  transition: background 0.3s;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: #bcc5d0;
}

/* ==================== 响应式适配 ==================== */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .stat-card {
    padding: 14px 16px;
  }

  .stat-value {
    font-size: 22px;
  }
}

@media (max-width: 992px) {
  .film-list {
    padding: 16px;
    gap: 14px;
  }

  .page-header,
  .table-card {
    padding: 18px;
    border-radius: 16px;
  }

  .stats-row {
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }

  .stat-card {
    padding: 12px 14px;
    gap: 10px;
    border-radius: 14px;
  }

  .stat-icon {
    width: 38px;
    height: 38px;
    border-radius: 10px;
  }

  .stat-icon svg {
    width: 18px;
    height: 18px;
  }

  .stat-value {
    font-size: 20px;
  }

  .message-card {
    padding: 18px 20px;
  }

  .message-status-badge {
    top: 12px;
    right: 14px;
    font-size: 11px;
    padding: 4px 10px;
  }

  .message-header {
    padding-right: 55px;
  }
}

@media (max-width: 768px) {
  .film-list {
    padding: 10px;
    gap: 10px;
  }

  .page-header {
    padding: 16px;
    border-radius: 14px;
  }

  .page-title {
    font-size: 20px;
  }

  .title-icon {
    font-size: 22px;
  }

  .header-decoration {
    display: none;
  }

  .stats-row {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }

  .stat-card {
    padding: 10px 12px;
    gap: 8px;
    border-radius: 12px;
  }

  .stat-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
  }

  .stat-icon svg {
    width: 16px;
    height: 16px;
  }

  .stat-value {
    font-size: 18px;
  }

  .stat-label {
    font-size: 11px;
  }

  .table-card {
    padding: 14px;
    border-radius: 14px;
  }

  .message-card {
    padding: 16px 14px;
    border-radius: 16px;
  }

  .message-status-badge {
    position: static;
    display: inline-flex;
    margin-bottom: 10px;
  }

  .message-header {
    padding-right: 0;
    gap: 10px;
    margin-bottom: 14px;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
  }

  .avatar-ring {
    inset: -3px;
  }

  .user-name {
    font-size: 14px;
  }

  .content-text,
  .reply-text {
    font-size: 14px;
  }

  .message-content {
    padding: 12px 14px;
    border-radius: 12px;
  }

  .message-reply {
    padding: 14px 16px;
    border-radius: 14px;
  }

  .message-reply::before {
    display: none;
  }

  .btn-reply,
  .btn-re {
    min-width: 100px;
    height: 34px;
    font-size: 13px;
  }

  .film-dialog {
    width: 94% !important;
  }

  .dialog-preview {
    padding: 12px 14px;
  }

  .pagination-container :deep(.el-pager li) {
    min-width: 30px;
    height: 30px;
    line-height: 30px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .stat-card {
    flex-direction: row;
    padding: 10px 14px;
  }

  .page-title {
    font-size: 18px;
  }

  .message-card {
    padding: 14px 12px;
  }

  .dialog-footer {
    flex-direction: column-reverse;
    gap: 8px;
  }

  .dialog-footer .el-button {
    width: 100%;
    min-width: unset;
  }
}
</style>