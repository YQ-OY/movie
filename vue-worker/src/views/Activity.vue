<template>
  <div class="film-list">

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">影院活动管理</div>
          <div class="page-subtitle">管理影院活动，支持新增、删除活动安排</div>
        </div>

        <div class="search-form">
          <el-button type="primary" class="search-submit-btn" @click="dialogFormVisible = true">
            <el-icon>
              <Plus />
            </el-icon>添加影院活动
          </el-button>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="paginatedList" class="film-table" stripe row-key="id"
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <!-- 展开列 -->
        <el-table-column type="expand" width="48">
          <template #default="props">
            <div class="expand-content">
              <div class="expand-section">
                <div class="expand-label">活动内容：</div>
                <div class="expand-text">{{ props.row.content }}</div>
              </div>
              <!-- 可扩展其他信息，如活动详情、参与人数等 -->
              <div class="expand-section" v-if="props.row.number">
                <div class="expand-label">参加人数：</div>
                <div class="expand-text">{{ props.row.number }} 人</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="开始时间" min-width="160" prop="startTime" align="center" />
        <el-table-column label="结束时间" min-width="160" prop="endTime" align="center" />
        <el-table-column label="活动内容" min-width="200" align="center">
          <template #default="scope">
            <span class="activity-summary">
              {{ scope.row.content.length > 30 ? scope.row.content.slice(0, 30) + '...' : scope.row.content }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="参加人数" min-width="120" prop="number" align="center" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="danger" plain @click="openDeleteDialog(scope.$index, scope.row)">
              <el-icon>
                <Delete />
              </el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" />
      </div>
    </div>

    <!-- 新增活动对话框 -->
    <el-dialog title="新增活动" v-model="dialogFormVisible" width="40%" align-center class="film-dialog film-dialog--edit">
      <el-form :model="form" label-width="100px" class="film-dialog__form">
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startTime" type="date" value-format="YYYY-MM-DD" placeholder="选择开始日期"
            class="full-width-input" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endTime" type="date" value-format="YYYY-MM-DD" placeholder="选择结束日期"
            class="full-width-input" />
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入活动内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="submitActivity">确定保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog title="删除活动" v-model="dialogDeleteVisible" width="28%" align-center
      class="film-dialog film-dialog--delete">
      <div class="delete-dialog-body">
        <div class="delete-dialog-body__icon">
          <el-icon>
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-dialog-body__content">
          <div class="delete-dialog-body__title">确认删除该活动？</div>
          <div class="delete-dialog-body__desc">删除后将无法恢复，活动内容：<strong>{{ deleteTarget.content }}</strong></div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer dialog-footer--danger">
          <el-button @click="dialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDeleteActivity">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { CreateActivity, DeleteActivityById, ListAllActivity } from "@/api/activity";
import { Plus, Delete, WarningFilled } from '@element-plus/icons-vue'

export default {
  name: 'ActivityManage',
  components: { Plus, Delete, WarningFilled },
  data() {
    return {
      loading: false,
      allList: [],        // 全量数据
      currentPage: 1,
      pageSize: 10,
      dialogFormVisible: false,
      form: {
        content: '',
        startTime: '',
        endTime: '',
      },
      dialogDeleteVisible: false,
      deleteTarget: { id: null, content: '' }
    }
  },
  computed: {
    totalCount() {
      return this.allList.length
    },
    paginatedList() {
      const start = (this.currentPage - 1) * this.pageSize
      return this.allList.slice(start, start + this.pageSize)
    }
  },
  mounted() {
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      ListAllActivity()
        .then(res => {
          if (!res?.success) return
          this.allList = res.data || []
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
    submitActivity() {
      if (!this.form.startTime) {
        this.$message.warning('请选择开始日期')
        return
      }
      if (!this.form.endTime) {
        this.$message.warning('请选择结束日期')
        return
      }
      if (!this.form.content) {
        this.$message.warning('请填写活动内容')
        return
      }
      CreateActivity(this.form)
        .then(res => {
          if (!res?.success) return
          this.$message.success('保存成功')
          this.dialogFormVisible = false
          this.form = { content: '', startTime: '', endTime: '' }
          this.loadList()
        })
        .catch(() => {})
    },
    openDeleteDialog(index, row) {
      this.deleteTarget = {
        id: row.id,
        content: row.content
      }
      this.dialogDeleteVisible = true
    },
    confirmDeleteActivity() {
      DeleteActivityById(this.deleteTarget.id)
        .then(res => {
          if (!res?.success) return
          this.$message.success('删除成功')
          this.dialogDeleteVisible = false
          if (this.paginatedList.length === 1 && this.currentPage > 1) {
            this.currentPage--
          }
          this.loadList()
        })
        .catch(() => {})
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

.search-submit-btn {
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
  letter-spacing: 0.2px;
  text-align: center;
  height: 58px;
}

.film-table :deep(.cell) {
  text-align: center;
}

.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: center;
}

.film-table :deep(.el-table__row) {
  transition: background-color 0.2s ease;
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

/* 对话框样式（统一复用 film-dialog） */
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

.film-dialog :deep(.el-dialog__headerbtn) {
  top: 18px;
  right: 18px;
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

.film-dialog__form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.film-dialog__form :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
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

/* 删除对话框专用样式 */
.film-dialog--delete :deep(.el-dialog__body) {
  padding: 20px 22px 8px;
}

.delete-dialog-body {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.delete-dialog-body__icon {
  width: 42px;
  height: 42px;
  flex: 0 0 auto;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fef2f2;
  color: #ef4444;
  font-size: 22px;
}

.delete-dialog-body__content {
  flex: 1;
  min-width: 0;
}

.delete-dialog-body__title {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.delete-dialog-body__desc {
  font-size: 13px;
  line-height: 1.7;
  color: #64748b;
}

.dialog-footer--danger .el-button:last-child {
  min-width: 108px;
}

/* 辅助类 */
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

/* 展开行样式 */
.expand-content {
  padding: 16px 20px;
  background: #fbfdff;
  border-radius: 12px;
  margin: 8px 0;
}

.expand-section {
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.expand-label {
  flex-shrink: 0;
  width: 80px;
  font-weight: 600;
  color: #64748b;
  font-size: 13px;
}

.expand-text {
  flex: 1;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  white-space: normal;
}

/* 活动内容摘要样式 */
.activity-summary {
  display: inline-block;
  max-width: 100%;
  color: #334155;
  cursor: pointer;
  transition: color 0.2s;
}
</style>