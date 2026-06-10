<template>
  <div class="film-list">

    <!-- 搜索区 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">用户管理</div>
          <div class="page-subtitle">支持按用户名搜索，管理用户信息及密码</div>
        </div>

        <div class="search-form">
          <el-input v-model="searchKeyword" clearable placeholder="用户名" class="search-item search-item--name"
            @keyup.enter="handleSearch" />
          <el-button type="primary" class="search-submit-btn" @click="handleSearch">
            <el-icon>
              <Search />
            </el-icon>
            <span>搜索</span>
          </el-button>
          <el-button class="search-reset-btn" @click="handleResetSearch">
            <el-icon>
              <Refresh />
            </el-icon>
            <span>重置</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="userList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <!-- 展开列 -->
        <el-table-column type="expand" width="48">
          <template #default="props">
            <div class="expand-detail expand-detail--with-aside">
              <div class="expand-detail__aside">
                <div class="expand-detail__full-title">用户头像</div>
                <div class="expand-detail__avatar-col">
                  <template v-if="isAvatarUrl(props.row.avatar)">
                    <el-image class="expand-detail__avatar-img" :src="props.row.avatar" fit="cover"
                      :preview-src-list="[props.row.avatar]" preview-teleported />
                  </template>
                  <span v-else class="expand-detail__value expand-detail__value--muted">未上传头像</span>
                </div>
              </div>
              <div class="expand-detail__body">
                <div class="expand-detail__grid">
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">用户名</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.username) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">昵称</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.nickname) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">密码</span>
                    <span class="expand-detail__value expand-detail__value--inline">
                      <el-button link type="primary" @click="openUpdatePassword(props.row)">
                        <el-icon>
                          <Edit />
                        </el-icon>编辑密码
                      </el-button>
                    </span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">手机号</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.phone) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">性别</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.gender) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">生日</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.birthday) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">个人简介</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.info) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">更新时间</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.updateAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="用户 ID" min-width="220" show-overflow-tooltip prop="id" />
        <el-table-column label="用户名" width="200" prop="username" />
        <el-table-column label="昵称" width="200" prop="nickname" />
        <el-table-column label="注册时间" min-width="180" prop="createAt" />
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" />
      </div>
    </div>
  </div>
</template>

<script>
import { listUserPage, UpdateUser } from '@/api/user'
import { Edit, Search, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'UserManage',
  components: { Edit, Search, Refresh },
  data() {
    return {
      loading: false,
      userList: [],        // 当前页数据
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
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
          keyword: this.searchKeyword || null,   // 后端支持按用户名/昵称模糊搜索
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
    openUpdatePassword(user) {
      this.$prompt('请输入新的密码', '修改密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'password',
        inputPattern: /^.{6,}$/,
        inputErrorMessage: '密码长度至少为6位'
      }).then(({ value }) => {
        // 调用更新接口
        const updateData = { ...user, password: value }
        UpdateUser(updateData).then(res => {
          if (!res?.success) return
          this.$message.success('密码修改成功')
          this.loadUsers()
        }).catch(() => {})
      }).catch(() => {
        this.$message.info('已取消修改')
      })
    }
  }
}
</script>

<style scoped>
/* 整体布局 - 与电影管理页面保持一致 */
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

.page-search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  flex-wrap: wrap;
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

.search-form {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  flex: 1;
  justify-content: flex-end;
}

.search-item {
  width: 200px;
}

.search-item--name {
  width: 240px;
}

.search-submit-btn,
.search-reset-btn {
  border-radius: 6px;
}

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

/* 展开区域样式 - 保持与参考页面一致 */
.expand-detail--with-aside {
  display: flex;
  gap: 24px;
  padding: 16px 24px;
  background: #fbfdff;
  border-radius: 16px;
}

.expand-detail__aside {
  flex: 0 0 120px;
  text-align: center;
}

.expand-detail__full-title {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 12px;
}

.expand-detail__avatar-col {
  display: flex;
  justify-content: center;
}

.expand-detail__avatar-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e2e8f0;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.05);
}

.expand-detail__body {
  flex: 1;
}

.expand-detail__grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 20px;
}

.expand-detail__item {
  display: flex;
  align-items: baseline;
  font-size: 13px;
  border-bottom: 1px dashed #edf2f7;
  padding-bottom: 8px;
}

.expand-detail__label {
  width: 80px;
  font-weight: 600;
  color: #64748b;
  flex-shrink: 0;
}

.expand-detail__value {
  flex: 1;
  color: #1e293b;
  word-break: break-word;
}

.expand-detail__value--inline {
  flex: none;
}

.expand-detail__value--muted {
  color: #94a3b8;
  font-style: italic;
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

/* 按钮图标与文字对齐 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* 让表格所有单元格内容默认居中（与电影管理页面一致） */
.film-table :deep(.cell) {
  text-align: center;
}

/* 第一列（展开列）内容保持左对齐，避免图标/按钮被居中 */
.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: left;
}

/* 展开行内部详情区域不受居中影响 */
.film-table :deep(.el-table__expanded-cell .cell) {
  text-align: left;
}
</style>