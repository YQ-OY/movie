<template>
  <div class="film-list">
    <!-- 搜索区 - 统一风格 -->
    <div class="page-header">
      <div class="search-container">
        <!-- 左侧：搜索筛选区域 -->
        <div class="search-filters">
          <el-input v-model="searchKeyword" clearable placeholder="工作内容" class="search-item search-item--name"
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
          <el-button type="success" class="btn-primary btn-add" @click="dialogFormVisible = true">
            <el-icon class="btn-icon">
              <Plus />
            </el-icon>
            <span>添加今日工作</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="workList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="添加时间" min-width="180" align="center" prop="createAt" />
        <el-table-column label="工作级别" min-width="140" align="center">
          <template #default="scope">
            <el-tag :type="getWorkLevelType(scope.row.type)" effect="light" :disable-transitions="true">
              {{ getWorkLevelText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="主要内容" min-width="400" prop="content" show-overflow-tooltip />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="danger" plain @click="openDeleteDialog(scope.$index, scope.row)">
              <el-icon>
                <Delete />
              </el-icon>
              <span>删除</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" :pager-count="5"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 添加工作对话框 -->
    <el-dialog title="添加今日工作" v-model="dialogFormVisible" width="520px" align-center
      class="film-dialog film-dialog--edit">
      <el-form :model="form" label-width="100px" class="film-dialog__form">
        <el-form-item label="工作级别">
          <el-select v-model="form.type" placeholder="请选择工作级别" style="width: 100%">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="非常重要" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="主要内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入工作内容" style="width: 100%"
            maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="saveDailyWork">确定保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog title="删除工作" v-model="dialogDeleteVisible" width="420px" align-center
      class="film-dialog film-dialog--delete" :close-on-click-modal="false">
      <div class="delete-dialog-body">
        <div class="delete-dialog-body__icon">
          <el-icon>
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-dialog-body__content">
          <div class="delete-dialog-body__title">确认删除该工作？</div>
          <div class="delete-dialog-body__desc">
            删除后将无法恢复
            <br />
            工作内容：<strong>{{ deleteTarget.content }}</strong>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { AddDailyWork, DeleteDailyWork, ListDailyWorkPage } from "@/api/worker";
import { Search, Refresh, Plus, Delete, WarningFilled } from '@element-plus/icons-vue'

export default {
  name: 'DailyWorkManage',
  components: { Search, Refresh, Plus, Delete, WarningFilled },
  data() {
    return {
      loading: false,
      workList: [],        // 当前页数据
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      dialogFormVisible: false,
      form: {
        type: 1,
        content: '',
      },
      dialogDeleteVisible: false,
      deleteTarget: { id: null, content: '' }
    }
  },
  mounted() {
    this.loadWorkList()
  },
  methods: {
    // 获取工作级别类型
    getWorkLevelType(type) {
      switch (type) {
        case 1: return 'info'
        case 2: return 'success'
        case 3: return 'danger'
        default: return 'info'
      }
    },
    // 获取工作级别文本
    getWorkLevelText(type) {
      switch (type) {
        case 1: return '普通'
        case 2: return '重要'
        case 3: return '非常重要'
        default: return '—'
      }
    },
    async loadWorkList() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword?.trim() || null
        }
        const res = await ListDailyWorkPage(params)
        if (!res?.success) return
        this.workList = res.data.rows || []
        this.totalCount = res.data.total || 0
        this.currentPage = res.data.page || this.currentPage
      } catch {
        // 全局 request 拦截器已提示
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadWorkList()
    },
    handleResetSearch() {
      this.searchKeyword = ''
      this.currentPage = 1
      this.loadWorkList()
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
      this.loadWorkList()
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadWorkList()
    },
    saveDailyWork() {
      if (!this.form.content?.trim()) {
        this.$message.warning('请填写工作内容')
        return
      }
      AddDailyWork(this.form).then(res => {
        if (!res?.success) return
        this.$message.success('保存成功')
        this.dialogFormVisible = false
        this.form = { type: 1, content: '' }
        this.currentPage = 1
        this.loadWorkList()
      }).catch(() => { })
    },
    openDeleteDialog(index, row) {
      this.deleteTarget = { id: row.id, content: row.content }
      this.dialogDeleteVisible = true
    },
    confirmDelete() {
      DeleteDailyWork(this.deleteTarget.id).then(res => {
        if (!res?.success) return
        this.$message.success('删除成功')
        this.dialogDeleteVisible = false
        if (this.workList.length === 1 && this.currentPage > 1) {
          this.currentPage--
        }
        this.loadWorkList()
      }).catch(() => { })
    }
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

/* 添加按钮特殊样式 */
.btn-add {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.25);
}

.btn-add:hover {
  background: linear-gradient(135deg, #059669 0%, #10b981 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.35);
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
  min-width: 72px;
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

/* 删除对话框样式 */
.delete-dialog-body {
  display: flex;
  align-items: flex-start;
  gap: 18px;
  padding: 10px 0;
}

.delete-dialog-body__icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #fef2f2;
  color: #ef4444;
  font-size: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.delete-dialog-body__content {
  flex: 1;
  min-width: 0;
}

.delete-dialog-body__title {
  font-size: 17px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 10px;
}

.delete-dialog-body__desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

.delete-dialog-body__desc strong {
  color: #334155;
  font-weight: 600;
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
}
</style>