<template>
  <div class="film-list">

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">影院留言</div>
          <div class="page-subtitle">查看用户留言，支持客服回复与管理</div>
        </div>
      </div>
    </div>

    <!-- 留言列表卡片 -->
    <div class="table-card">
      <div v-if="paginatedList.length === 0" class="empty-state">
        <el-empty description="暂无留言" :image-size="120" />
      </div>
      <div v-else class="message-list">
        <div v-for="(item, idx) in paginatedList" :key="idx" class="message-card">
          <div class="message-header">
            <img class="user-avatar" :src="item.user.avatar" alt="avatar" />
            <div class="user-info">
              <div class="user-name">{{ item.user.nickname || '匿名用户' }}</div>
              <div class="message-time">时间：{{ item.leavingMessage.createAt }}</div>
            </div>
          </div>
          <div class="message-content">
            <div class="content-label">留言内容：</div>
            <div class="content-text">{{ item.leavingMessage.content }}</div>
          </div>
          <div class="message-reply" v-if="item.leavingMessage.reply">
            <div class="reply-label">客服回复：</div>
            <div class="reply-text">{{ item.leavingMessage.reply }}</div>
          </div>
          <div class="message-actions">
            <el-button size="small" type="primary" plain :icon="ChatDotRound" @click="handleReply(item, idx)">
              回复留言
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" />
      </div>
    </div>

    <!-- 回复留言对话框 -->
    <el-dialog title="回复留言" v-model="dialogFormVisible" width="40%" align-center class="film-dialog film-dialog--edit">
      <el-form :model="form" label-width="80px" class="film-dialog__form">
        <el-form-item label="回复内容">
          <el-input v-model="form.reply" type="textarea" :rows="6" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">确定提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ListAllLeavingMessage, ReplyLeavingMessage } from "@/api/leaving";
import { ChatDotRound } from '@element-plus/icons-vue'

export default {
  name: 'LeavingMessage',
  components: { ChatDotRound },
  data() {
    return {
      loading: false,
      messageList: [],       // 全量留言数据
      currentPage: 1,
      pageSize: 5,
      dialogFormVisible: false,
      form: {
        id: null,
        reply: ''
      }
    }
  },
  computed: {
    totalCount() {
      return this.messageList.length
    },
    paginatedList() {
      const start = (this.currentPage - 1) * this.pageSize
      return this.messageList.slice(start, start + this.pageSize)
    }
  },
  mounted() {
    this.loadMessages()
  },
  methods: {
    loadMessages() {
      this.loading = true
      ListAllLeavingMessage()
        .then(res => {
          if (!res?.success) return
          this.messageList = res.data || []
          this.currentPage = 1
        })
        .catch(() => {})
        .finally(() => {
          this.loading = false
        })
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
    },
    handleReply(item, index) {
      this.form = {
        id: item.leavingMessage.id,
        reply: item.leavingMessage.reply || ''
      }
      this.dialogFormVisible = true
    },
    submitReply() {
      if (!this.form.reply) {
        this.$message.warning('请填写回复内容')
        return
      }
      ReplyLeavingMessage(this.form).then(res => {
        if (!res?.success) return
        this.$message.success('回复成功')
        this.dialogFormVisible = false
        this.loadMessages()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
/* ===== 整体布局（复用电影管理页面样式） ===== */
.film-list {
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
  padding: 20px;
  box-sizing: border-box;
  background: rgb(250, 251, 252);
}

.page-header {
  flex: 0 0 auto;
  padding: 20px 22px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.page-search-bar__title {
  min-width: 220px;
}

.page-title {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: 0.2px;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

/* 表格卡片（留言列表容器） */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 18px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

/* 留言卡片列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-right: 4px;
}

/* 单个留言卡片 */
.message-card {
  background: #ffffff;
  border: 1px solid #eef2f7;
  border-radius: 20px;
  padding: 18px 20px;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.message-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

/* 卡片头部（头像+昵称+时间） */
.message-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.message-time {
  font-size: 12px;
  color: #94a3b8;
}

/* 留言内容区域 */
.message-content,
.message-reply {
  margin-bottom: 14px;
  padding-left: 8px;
  border-left: 3px solid #e2e8f0;
}

.content-label,
.reply-label {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 6px;
}

.content-text,
.reply-text {
  font-size: 14px;
  line-height: 1.6;
  color: #1e293b;
  word-break: break-word;
}

.message-reply {
  border-left-color: #3b82f6;
  background: #f8fafc;
  padding: 12px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.reply-label {
  color: #3b82f6;
}

/* 操作按钮 */
.message-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.message-actions .el-button {
  border-radius: 40px;
  min-width: 100px;
  height: 34px;
  font-size: 13px;
}

/* 分页样式 */
.pagination-container {
  flex-shrink: 0;
  margin-top: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #e2e8f0;
  padding: 15px 0 0;
  margin-top: 18px;
}

.pagination-container :deep(.el-pagination) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

/* 对话框样式（复用 film-dialog） */
.film-dialog {
  border-radius: 18px;
  overflow: hidden;
}

.film-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 14px;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-bottom: 1px solid #e2e8f0;
}

.film-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}

.film-dialog :deep(.el-dialog__body) {
  padding: 20px 24px 12px;
  background: #fbfdff;
}

.film-dialog :deep(.el-dialog__footer) {
  padding: 14px 22px 22px;
  background: #fbfdff;
  border-top: 1px solid #e2e8f0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer .el-button {
  min-width: 92px;
  height: 38px;
  border-radius: 10px;
}

/* 滚动条美化 */
.message-list::-webkit-scrollbar {
  width: 5px;
}

.message-list::-webkit-scrollbar-track {
  background: #eef2f8;
  border-radius: 10px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
</style>