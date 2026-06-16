<template>
  <div class="film-list">
    <!-- 搜索区 - 统一风格 -->
    <div class="page-header">
      <div class="search-container">
        <!-- 左侧：搜索筛选区域 -->
        <div class="search-filters">
          <el-input v-model="searchKeyword" clearable placeholder="用户名 / 昵称" class="search-item search-item--name"
            @keyup.enter="handleSearch" />
        </div>

        <!-- 右侧：操作按钮区域 -->
        <div class="search-actions">
          <el-button type="primary" class="btn-primary btn-search" @click="handleSearch">
            <el-icon class="btn-icon">
              <Search />
            </el-icon>
            <span>搜索</span>
          </el-button>
          <el-button class="btn-secondary btn-reset" @click="handleResetSearch">
            <el-icon class="btn-icon">
              <Refresh />
            </el-icon>
            <span>重置</span>
          </el-button>
          <div class="btn-divider"></div>
          <el-button class="btn-secondary btn-export" @click="exportUsers">
            <el-icon class="btn-icon">
              <Download />
            </el-icon>
            <span>导出报表</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="userList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="用户 ID" min-width="220" show-overflow-tooltip prop="id" />
        <el-table-column label="用户名" width="200" prop="username" />
        <el-table-column label="昵称" width="200" prop="nickname" />
        <el-table-column label="注册时间" min-width="180" prop="createAt" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="openDetailDialog(scope.row)">
                <el-icon>
                  <View />
                </el-icon>
                <span>详情</span>
              </el-button>
              <el-button size="small" type="warning" plain @click="openUpdatePassword(scope.row)">
                <el-icon>
                  <Edit />
                </el-icon>
                <span>改密</span>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" />
      </div>
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog title="用户详情" v-model="detailDialogVisible" width="70%" align-center
      class="film-dialog film-dialog--detail">
      <!-- 全局图片预览组件 -->
      <el-image-viewer v-if="previewVisible" :url-list="[previewUrl]" :initial-index="0" teleported
        @close="closeImagePreview" />

      <div class="detail-layout">
        <!-- 左侧头像区域 - 修复无头像布局 -->
        <!-- 左侧头像区域 -->
        <div class="detail-aside">
          <div class="avatar-card">
            <div class="avatar-card__header">
              <span class="avatar-card__title">用户头像</span>
            </div>
            <div class="avatar-card__body">
              <template v-if="isAvatarUrl(currentUser.avatar)">
                <div class="avatar-wrapper" @click="openImagePreview(currentUser.avatar)">
                  <img class="detail-avatar-img" :src="currentUser.avatar" alt="用户头像" />
                  <div class="avatar-overlay">
                    <el-icon class="avatar-overlay__icon">
                      <View />
                    </el-icon>
                    <span class="avatar-overlay__text">点击预览</span>
                  </div>
                </div>
              </template>
              <template v-else>
                <!-- 使用项目logo作为默认头像 -->
                <div class="default-avatar-wrapper">
                  <img class="detail-avatar-img default-avatar-img" :src="require('@/assets/img/logo.png')"
                    alt="默认头像" />
                  <span class="default-avatar-text">未上传头像</span>
                </div>
              </template>
            </div>
          </div>
        </div>

        <!-- 右侧信息区域 -->
        <div class="detail-body">
          <div class="info-card">
            <div class="info-card__header">
              <span class="info-card__title">基本信息</span>
              <el-tag :type="currentUser.status !== false ? 'success' : 'danger'" effect="light"
                class="info-card__status">
                {{ currentUser.status !== false ? '正常用户' : '已禁用' }}
              </el-tag>
            </div>
            <div class="info-card__grid">
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <User />
                  </el-icon>
                  <span>用户ID</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.id) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <UserFilled />
                  </el-icon>
                  <span>用户名</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.username) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <User />
                  </el-icon>
                  <span>昵称</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.nickname) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Phone />
                  </el-icon>
                  <span>手机号</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.phone) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Avatar />
                  </el-icon>
                  <span>性别</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.gender) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Star />
                  </el-icon>
                  <span>生日</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.birthday) }}</div>
              </div>
              <div class="info-item info-item--full">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Document />
                  </el-icon>
                  <span>个人简介</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.info) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Clock />
                  </el-icon>
                  <span>注册时间</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.createAt) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <RefreshRight />
                  </el-icon>
                  <span>更新时间</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentUser.updateAt) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="openUpdatePassword(currentUser)">
            <el-icon>
              <Edit />
            </el-icon>
            修改密码
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listUserPage, UpdateUser } from '@/api/user'
import { ElImageViewer } from 'element-plus'
import {
  View, User, UserFilled, Phone, Avatar,
  Star, Document, Clock, Edit, Search, Refresh,
  Download, RefreshRight
} from '@element-plus/icons-vue'
import { downloadCsv } from '@/utils/exportCsv'

export default {
  name: 'UserManage',
  components: {
    ElImageViewer,
    View, User, UserFilled, Phone, Avatar,
    Star, Document, Clock, Edit, Search, Refresh,
    Download, RefreshRight
  },
  data() {
    return {
      loading: false,
      userList: [],
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      detailDialogVisible: false,
      currentUser: {},
      previewVisible: false,
      previewUrl: '',
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword?.trim() || null,
        }
        const res = await listUserPage(params)
        if (!res?.success) return
        this.userList = res.data.rows || []
        this.totalCount = res.data.total || 0
        this.currentPage = res.data.page || this.currentPage
      } catch (error) {
        console.error('加载用户列表失败', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadUsers()
    },
    handleResetSearch() {
      this.searchKeyword = ''
      this.currentPage = 1
      this.loadUsers()
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
      this.loadUsers()
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadUsers()
    },
    formatCell(val) {
      if (val == null) return '—'
      const s = String(val).trim()
      if (s === '' || s === 'null') return '—'
      return s
    },
    isAvatarUrl(avatar) {
      if (avatar == null) return false
      const s = String(avatar).trim()
      if (s === '' || s === 'null') return false
      return /^https?:\/\//i.test(s) || s.startsWith('/')
    },
    exportUsers() {
      const rows = (this.userList || []).map((item) => ([
        item.id ?? '—',
        item.username ?? '—',
        item.nickname ?? '—',
        item.phone ?? '—',
        item.gender ?? '—',
        item.birthday ?? '—',
        item.createAt ?? '—',
        item.updateAt ?? '—',
      ]))
      downloadCsv(
        `用户列表_${new Date().toISOString().slice(0, 10)}.csv`,
        ['用户ID', '用户名', '昵称', '手机号', '性别', '生日', '注册时间', '更新时间'],
        rows
      )
    },
    openUpdatePassword(user) {
      this.$prompt('请输入新的密码', '修改密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'password',
        inputPattern: /^.{6,}$/,
        inputErrorMessage: '密码长度至少为6位'
      }).then(({ value }) => {
        const updateData = { ...user, password: value }
        UpdateUser(updateData).then(res => {
          if (!res?.success) return
          this.$message.success('密码修改成功')
          this.detailDialogVisible = false
          this.loadUsers()
        }).catch(() => { })
      }).catch(() => {
        this.$message.info('已取消修改')
      })
    },
    openDetailDialog(user) {
      this.currentUser = { ...user }
      this.detailDialogVisible = true
    },
    openImagePreview(url) {
      this.previewUrl = url
      this.previewVisible = true
    },
    closeImagePreview() {
      this.previewVisible = false
    },
  }
}
</script>

<style scoped>
/* 整体布局 - 统一风格 */
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

/* 搜索区整体容器 - 统一风格 */
.page-header {
  margin-bottom: 0;
  padding: 20px 24px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
}

/* 搜索区域主容器 - 左右分栏 */
.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

/* 左侧搜索输入区域 */
.search-filters {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  flex: 1;
  min-width: 0;
}

/* 右侧操作按钮区域 */
.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

/* 按钮分隔线 */
.btn-divider {
  width: 1px;
  height: 32px;
  background-color: #e2e8f0;
  margin: 0 4px;
}

/* 搜索项样式统一 */
.search-item {
  height: 40px;
}

.search-item--name {
  width: 280px;
}

/* 输入框和选择框样式优化 - 统一风格 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #93c5fd;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 基础按钮样式 - 统一风格 */
.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  padding: 0 18px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  box-shadow: none;
}

/* 按钮图标样式 */
.btn-icon {
  font-size: 16px;
  flex-shrink: 0;
}

/* 主要按钮样式 */
.btn-primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #337ecc 0%, #409eff 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
}

.btn-primary:active {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.3);
}

/* 次要按钮样式 */
.btn-secondary {
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f1f5f9;
  color: #334155;
  border-color: #cbd5e1;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.btn-secondary:active {
  transform: translateY(0);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

/* 表格卡片 */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 18px 18px 0;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.film-table {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  border-radius: 16px;
}

/* 表头样式 */
.film-table :deep(.el-table__header-wrapper th) {
  border-bottom: 1px solid #e2e8f0;
  font-size: 15px;
  font-weight: 700;
  text-align: center;
  height: 58px;
}

/* 单元格居中 */
.film-table :deep(.cell) {
  text-align: center;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.action-buttons :deep(.el-button) {
  border-radius: 10px;
  min-width: 76px;
  height: 34px;
  font-size: 13px;
  flex: 0 0 auto;
}

/* 分页样式 */
.pagination-container {
  flex-shrink: 0;
  margin-top: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #e2e8f0;
  padding: 15px;
}

.pagination-container :deep(.el-pagination) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
  width: 100%;
}

.pagination-container :deep(.el-pagination__total),
.pagination-container :deep(.el-pagination__sizes),
.pagination-container :deep(.el-pagination__jump) {
  margin: 0;
  color: #64748b;
}

.pagination-container :deep(.el-pager li),
.pagination-container :deep(.btn-prev),
.pagination-container :deep(.btn-next) {
  border-radius: 8px;
  min-width: 32px;
  margin: 0 3px;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

/* 对话框通用样式 */
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

/* 详情对话框布局 - 修复高度对齐 */
.detail-layout {
  display: flex;
  gap: 24px;
  min-height: 420px;
}

.detail-aside {
  flex: 0 0 200px;
  display: flex;
  flex-direction: column;
}

.detail-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* 头像卡片 - 修复固定高度 */
.avatar-card {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.avatar-card__header {
  padding: 14px 16px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  text-align: center;
  flex-shrink: 0;
}

.avatar-card__title {
  font-size: 14px;
  font-weight: 700;
  color: #ffffff;
}

.avatar-card__body {
  padding: 24px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

/* 头像容器与悬浮效果 */
.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.detail-avatar-img {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 默认头像样式 */
.default-avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  width: 100%;
  height: 100%;
}

.default-avatar-img {
  /* 继承真实头像的所有样式 */
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  /* 默认头像添加轻微的灰度效果，区分真实头像 */
  filter: grayscale(20%);
  opacity: 0.9;
}

.default-avatar-text {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.avatar-overlay__icon {
  font-size: 28px;
  color: #ffffff;
  margin-bottom: 6px;
}

.avatar-overlay__text {
  font-size: 13px;
  color: #ffffff;
  font-weight: 500;
}

.avatar-wrapper:hover .detail-avatar-img {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.18);
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

/* 修复默认头像布局 */
.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  width: 100%;
  height: 100%;
}

.avatar-placeholder__circle {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: #f1f5f9;
  border: 4px solid #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-placeholder__icon {
  font-size: 70px;
  color: #cbd5e1;
}

.avatar-placeholder__text {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
}

/* 信息卡片 */
.info-card {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.info-card__header {
  padding: 14px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

.info-card__title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.info-card__status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 8px;
}

/* 信息网格布局 */
.info-card__grid {
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 24px;
  flex: 1;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item--full {
  grid-column: 1 / -1;
}

.info-item__label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
}

.info-item__icon {
  font-size: 14px;
  color: #409eff;
}

.info-item__value {
  font-size: 14px;
  color: #1e293b;
  word-break: break-word;
  line-height: 1.5;
}

/* 按钮内图标和文字对齐 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .search-container {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .search-filters {
    justify-content: center;
  }

  .search-actions {
    justify-content: center;
  }

  .detail-layout {
    flex-direction: column;
    gap: 20px;
    min-height: auto;
  }

  .detail-aside {
    flex: none;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 16px;
  }

  .search-item--name {
    width: 240px;
  }

  .btn-divider {
    display: none;
  }

  .info-card__grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .film-list {
    padding: 12px;
  }

  .search-actions {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }

  .el-button {
    width: 100%;
    padding: 0 12px;
    font-size: 13px;
  }

  .detail-avatar-img,
  .avatar-placeholder__circle {
    width: 100px;
    height: 100px;
  }

  .avatar-placeholder__icon {
    font-size: 50px;
  }
}
</style>