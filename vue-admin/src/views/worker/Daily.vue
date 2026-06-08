<template>
  <div class="film-list">

    <!-- 搜索区（简单版，可扩展） -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">每日工作管理</div>
          <div class="page-subtitle">管理每日工作计划，支持按级别 / 内容筛选</div>
        </div>

        <div class="search-form">
          <el-input v-model="searchKeyword" clearable placeholder="工作内容" class="search-item search-item--name"
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
          <el-button type="success" class="search-submit-btn" @click="dialogFormVisible = true">
            <el-icon>
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
        <el-table-column label="工作级别" min-width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.type === 1" type="info" effect="light">普通</el-tag>
            <el-tag v-else-if="scope.row.type === 2" type="success" effect="light">重要</el-tag>
            <el-tag v-else-if="scope.row.type === 3" type="danger" effect="light">非常重要</el-tag>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="主要内容" min-width="400" prop="content" show-overflow-tooltip />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="danger" plain :icon="Delete"
              @click="openDeleteDialog(scope.$index, scope.row)">
              删除
            </el-button>
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

    <!-- 添加 / 编辑对话框（统一风格） -->
    <el-dialog title="添加今日工作" v-model="dialogFormVisible" width="40%" align-center
      class="film-dialog film-dialog--edit">
      <el-form :model="form" label-width="100px" class="film-dialog__form">
        <el-form-item label="工作级别">
          <el-select v-model="form.type" placeholder="请选择工作级别" class="full-width-input">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="非常重要" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="主要内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入工作内容" class="full-width-input" />
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
    <el-dialog title="删除工作" v-model="dialogDeleteVisible" width="28%" align-center
      class="film-dialog film-dialog--delete">
      <div class="delete-dialog-body">
        <div class="delete-dialog-body__icon">
          <el-icon>
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-dialog-body__content">
          <div class="delete-dialog-body__title">确认删除该工作？</div>
          <div class="delete-dialog-body__desc">删除后将无法恢复，工作内容：<strong>{{ deleteTarget.content }}</strong></div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer dialog-footer--danger">
          <el-button @click="dialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { AddDailyWork, DeleteDailyWork, ListDailyWorkPage } from "@/api/worker";  // 需改为分页接口
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
    async loadWorkList() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword || null
        }
        const res = await ListDailyWorkPage(params)
        if (res.success) {
          this.workList = res.data.rows || []
          this.totalCount = res.data.total || 0
          this.currentPage = res.data.page || this.currentPage
        } else {
          this.$message.error(res.msg || '加载失败')
        }
      } catch (error) {
        this.$message.error('加载失败')
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
      if (!this.form.content) {
        this.$message.warning('请填写工作内容')
        return
      }
      AddDailyWork(this.form).then(() => {
        this.$message.success('保存成功')
        this.dialogFormVisible = false
        this.form = { type: 1, content: '' }
        // 重置到第一页并刷新
        this.currentPage = 1
        this.loadWorkList()
      }).catch(() => {
        this.$message.error('保存失败')
      })
    },
    openDeleteDialog(index, row) {
      this.deleteTarget = { id: row.id, content: row.content }
      this.dialogDeleteVisible = true
    },
    confirmDelete() {
      DeleteDailyWork(this.deleteTarget.id).then(() => {
        this.$message.success('删除成功')
        this.dialogDeleteVisible = false
        // 如果当前页只剩一条数据且不是第一页，跳到上一页
        if (this.workList.length === 1 && this.currentPage > 1) {
          this.currentPage--
        }
        this.loadWorkList()
      }).catch(() => {
        this.$message.error('删除失败')
      })
    }
  }
}
</script>

<style scoped>
/* 完全复用电影管理页面的样式（以下为必要部分，实际使用时复制完整样式） */
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
  width: 260px;
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
  padding: 18px 18px 0px 18px;
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

.film-table :deep(.el-table__header-wrapper th) {
  border-bottom: 1px solid #e2e8f0;
  font-size: 15px;
  font-weight: 700;
  text-align: center;
  height: 58px;
}

.film-table :deep(.cell) {
  text-align: center;
}

.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: left;
}

.film-table :deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

.film-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(148, 163, 184, 0.14) !important;
}

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

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.film-dialog {
  border-radius: 18px;
  overflow: hidden;
}

.film-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-bottom: 1px solid #e2e8f0;
}

.film-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}

.film-dialog :deep(.el-dialog__body) {
  background: #fbfdff;
  padding: 20px 24px 12px;
}

.film-dialog :deep(.el-dialog__footer) {
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

.delete-dialog-body {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.delete-dialog-body__icon {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #fef2f2;
  color: #ef4444;
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-dialog-body__title {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.delete-dialog-body__desc {
  font-size: 13px;
  color: #64748b;
}

.full-width-input {
  width: 100%;
}

.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>