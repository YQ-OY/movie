<template>
  <div class="activity-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-line">
          <span class="title-icon">🎬</span>
          <h1 class="page-title">影院活动管理</h1>
        </div>
        <p class="page-subtitle">统一管理营销活动，支持新增、删除与详情查看</p>
      </div>
      <div class="header-actions">
        <!-- 统计小卡片 -->
        <div class="quick-stats">
          <div class="quick-stat">
            <span class="quick-stat__value">{{ totalCount }}</span>
            <span class="quick-stat__label">活动总数</span>
          </div>
          <div class="quick-stat">
            <span class="quick-stat__value">{{ upcomingCount }}</span>
            <span class="quick-stat__label">即将开始</span>
          </div>
        </div>
        <el-button type="primary" class="btn-add" @click="openAddDialog">
          <el-icon>
            <Plus />
          </el-icon>
          <span>添加影院活动</span>
        </el-button>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card" v-loading="loading" element-loading-text="正在加载活动列表...">
      <el-table :data="paginatedList" class="activity-table" stripe row-key="id" :header-cell-style="headerCellStyle"
        :row-style="{ height: '78px' }" highlight-current-row>
        <!-- 展开详情列 -->
        <el-table-column type="expand" width="48">
          <template #default="props">
            <div class="expand-panel">
              <div class="expand-header">
                <span class="expand-badge">活动详情</span>
              </div>
              <div class="expand-grid">
                <div class="expand-item">
                  <span class="expand-label">活动内容</span>
                  <span class="expand-text">{{ props.row.content }}</span>
                </div>
                <div class="expand-item" v-if="props.row.number !== undefined">
                  <span class="expand-label">参与人数</span>
                  <span class="expand-text highlight">{{ props.row.number }} 人</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 活动开始 -->
        <el-table-column label="活动开始" min-width="160" prop="startTime" align="center">
          <template #default="scope">
            <div class="date-cell">
              <el-icon class="date-icon">
                <Calendar />
              </el-icon>
              <span>{{ scope.row.startTime }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 活动结束 -->
        <el-table-column label="活动结束" min-width="160" prop="endTime" align="center">
          <template #default="scope">
            <div class="date-cell">
              <el-icon class="date-icon">
                <Clock />
              </el-icon>
              <span>{{ scope.row.endTime }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 活动简介 -->
        <el-table-column label="活动简介" min-width="210" align="center">
          <template #default="scope">
            <span class="summary-text">
              {{ scope.row.content.length > 28 ? scope.row.content.slice(0, 28) + '…' : scope.row.content }}
            </span>
          </template>
        </el-table-column>

        <!-- 参与人数 -->
        <el-table-column label="参与人数" min-width="120" prop="number" align="center">
          <template #default="scope">
            <el-tag type="info" effect="plain" round size="small" class="number-tag">
              {{ scope.row.number ?? 0 }} 人
            </el-tag>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="danger" plain :icon="Delete" @click="openDeleteDialog(scope.row)"
              class="btn-delete">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" background />
      </div>
    </div>

    <!-- 新增/编辑活动弹窗 -->
    <el-dialog title="新增影院活动" v-model="dialogFormVisible" width="46%" align-center class="activity-dialog"
      :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" class="dialog-form">
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startTime" type="date" value-format="YYYY-MM-DD" placeholder="请选择活动开始日期"
            class="full-width-input" size="large" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endTime" type="date" value-format="YYYY-MM-DD" placeholder="请选择活动结束日期"
            class="full-width-input" size="large" />
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="详细填写活动规则、福利、参与方式等内容" maxlength="500"
            show-word-limit size="large" class="content-textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogFormVisible = false" class="btn-cancel">取消</el-button>
          <el-button size="large" type="primary" @click="submitActivity" class="btn-submit">
            <el-icon>
              <Check />
            </el-icon>
            确认保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog title="删除活动" v-model="dialogDeleteVisible" width="32%" align-center class="activity-dialog delete-dialog"
      :close-on-click-modal="false">
      <div class="delete-body">
        <div class="delete-icon-wrap">
          <el-icon size="26">
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-content">
          <p class="delete-title">确定删除这条活动？</p>
          <p class="delete-desc">
            删除后将无法恢复，活动内容：<strong>{{ deleteTarget.content }}</strong>
          </p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogDeleteVisible = false">取消</el-button>
          <el-button size="large" type="danger" @click="confirmDeleteActivity" class="btn-danger">
            永久删除
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus,
  Delete,
  WarningFilled,
  Calendar,
  Clock,
  Check
} from '@element-plus/icons-vue'
import {
  CreateActivity,
  DeleteActivityById,
  ListAllActivity
} from '@/api/activity'

// ---------- 状态 ----------
const loading = ref(false)
const allList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// 新增弹窗
const dialogFormVisible = ref(false)
const form = reactive({
  content: '',
  startTime: '',
  endTime: ''
})

// 删除弹窗
const dialogDeleteVisible = ref(false)
const deleteTarget = reactive({ id: null, content: '' })

// ---------- 计算属性 ----------
const totalCount = computed(() => allList.value.length)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return allList.value.slice(start, start + pageSize.value)
})

// 即将开始的活动数量（根据日期简单判断）
const upcomingCount = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return allList.value.filter(item => {
    if (!item.startTime) return false
    const start = new Date(item.startTime)
    return start >= today
  }).length
})

// 表头样式（保持统一）
const headerCellStyle = {
  background: 'linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%)',
  color: '#1e293b',
  fontWeight: 700,
  fontSize: '14px',
  textAlign: 'center',
  height: '56px',
  borderBottom: '2px solid #e2e8f0'
}

// ---------- 方法 ----------
function loadList() {
  loading.value = true
  ListAllActivity()
    .then(res => {
      if (!res?.success) return
      allList.value = res.data || []
      currentPage.value = 1
    })
    .catch(err => {
      console.error('加载活动列表失败', err)
      ElMessage.error('加载活动失败，请重试')
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

function openAddDialog() {
  form.content = ''
  form.startTime = ''
  form.endTime = ''
  dialogFormVisible.value = true
}

function submitActivity() {
  const start = form.startTime?.trim()
  const end = form.endTime?.trim()
  const content = form.content?.trim()

  if (!start) return ElMessage.warning('请选择活动开始日期')
  if (!end) return ElMessage.warning('请选择活动结束日期')
  if (!content) return ElMessage.warning('请填写完整活动内容')

  CreateActivity({ startTime: start, endTime: end, content })
    .then(res => {
      if (!res?.success) return
      ElMessage.success('活动创建成功 🎉')
      dialogFormVisible.value = false
      loadList()
    })
    .catch(err => {
      console.error('新增活动失败', err)
      ElMessage.error('新增失败，请稍后重试')
    })
}

function openDeleteDialog(row) {
  deleteTarget.id = row.id
  deleteTarget.content = row.content?.length > 20 ? row.content.slice(0, 20) + '…' : row.content
  dialogDeleteVisible.value = true
}

function confirmDeleteActivity() {
  DeleteActivityById(deleteTarget.id)
    .then(res => {
      if (!res?.success) return
      ElMessage.success('活动已删除')
      dialogDeleteVisible.value = false
      // 当前页删掉最后一条，回退一页
      if (paginatedList.value.length === 1 && currentPage.value > 1) {
        currentPage.value--
      }
      loadList()
    })
    .catch(err => {
      console.error('删除活动失败', err)
      ElMessage.error('删除失败，请重试')
    })
}

// ---------- 生命周期 ----------
onMounted(() => {
  loadList()
})
</script>

<style scoped>
/* ==================== 整体布局 ==================== */
.activity-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 22px 24px;
  box-sizing: border-box;
  background: #f7f9fc;
  background-image:
    radial-gradient(ellipse at 15% 30%, rgba(99, 102, 241, 0.04) 0%, transparent 55%),
    radial-gradient(ellipse at 85% 70%, rgba(59, 130, 246, 0.03) 0%, transparent 55%);
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
}

/* ==================== 页面头部 ==================== */
.page-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 26px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow:
    0 8px 28px rgba(15, 23, 42, 0.05),
    0 2px 6px rgba(15, 23, 42, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.7);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title-line {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 28px;
  filter: drop-shadow(0 4px 6px rgba(59, 130, 246, 0.2));
}

.page-title {
  margin: 0;
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.3px;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  padding-left: 38px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

/* 统计小卡片 */
.quick-stats {
  display: flex;
  gap: 10px;
}

.quick-stat {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  padding: 10px 18px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
}

.quick-stat__value {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
}

.quick-stat__label {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

/* 添加按钮 */
.btn-add {
  height: 44px;
  padding: 0 24px;
  border-radius: 12px;
  font-weight: 600;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border: none;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-add:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

/* ==================== 表格卡片 ==================== */
.table-card {
  flex: 1;
  min-height: 0;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 22px;
  padding: 20px 22px 0;
  box-shadow:
    0 8px 28px rgba(15, 23, 42, 0.05),
    0 2px 6px rgba(15, 23, 42, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.7);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.activity-table {
  flex: 1;
  min-height: 0;
  border-radius: 16px;
}

/* 表格细节 */
.activity-table :deep(.el-table__header-wrapper th) {
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.activity-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(241, 245, 249, 0.7) !important;
}

.activity-table :deep(.el-table__row--striped td) {
  background-color: #fafbfd;
}

.activity-table :deep(.el-table__cell .cell) {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 展开行 */
.expand-panel {
  padding: 16px 24px;
  background: #f8fafc;
  border-radius: 14px;
  margin: 8px 0;
  border: 1px solid #f1f5f9;
}

.expand-header {
  margin-bottom: 14px;
}

.expand-badge {
  background: #e0e7ff;
  color: #3730a3;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.expand-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.expand-item {
  display: flex;
  gap: 16px;
}

.expand-label {
  width: 90px;
  font-weight: 600;
  color: #64748b;
  flex-shrink: 0;
}

.expand-text {
  color: #1e293b;
  line-height: 1.6;
  word-break: break-word;
}

.expand-text.highlight {
  color: #2563eb;
  font-weight: 700;
}

/* 日期单元格 */
.date-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.date-icon {
  font-size: 16px;
  color: #94a3b8;
}

/* 简介文本 */
.summary-text {
  color: #334155;
  font-size: 14px;
}

/* 人数标签 */
.number-tag {
  font-weight: 600;
  background: #f1f5f9;
  border-color: #e2e8f0;
  color: #475569;
}

/* 删除按钮 */
.btn-delete {
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.25s ease;
}

.btn-delete:hover {
  background: #fef2f2;
  border-color: #fca5a5;
  color: #dc2626;
}

/* 分页 */
.pagination-container {
  flex-shrink: 0;
  margin-top: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #e8edf2;
  padding: 16px 8px;
}

.pagination-container :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination-container :deep(.el-pager li) {
  border-radius: 10px;
  font-weight: 600;
  min-width: 34px;
  height: 34px;
  background: #fff;
  border: 1px solid #e2e8f0;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border: none;
  color: #fff;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.35);
}

/* ==================== 弹窗通用样式 ==================== */
.activity-dialog {
  border-radius: 24px !important;
  overflow: hidden;
}

.activity-dialog :deep(.el-dialog__header) {
  padding: 24px 28px 18px;
  background: linear-gradient(135deg, #f8fafc, #ffffff);
  border-bottom: 1px solid #eef2f7;
}

.activity-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}

.activity-dialog :deep(.el-dialog__body) {
  padding: 22px 28px 12px;
  background: #fbfdff;
}

.activity-dialog :deep(.el-dialog__footer) {
  padding: 16px 28px 22px;
  border-top: 1px solid #eef2f7;
}

.dialog-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
}

.full-width-input {
  width: 100%;
}

.content-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  background: #fafcfd;
  transition: all 0.3s;
}

.content-textarea :deep(.el-textarea__inner:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel {
  border-radius: 12px;
  font-weight: 600;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.btn-submit {
  border-radius: 12px;
  font-weight: 600;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border: none;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.3);
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-submit:hover {
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

/* 删除弹窗 */
.delete-dialog :deep(.el-dialog__body) {
  padding: 24px 28px 10px;
}

.delete-body {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.delete-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #fef2f2;
  color: #ef4444;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.delete-title {
  font-size: 17px;
  font-weight: 700;
  margin: 0 0 8px;
  color: #0f172a;
}

.delete-desc {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

.delete-desc strong {
  color: #dc2626;
  font-weight: 600;
}

.btn-danger {
  border-radius: 12px;
  font-weight: 600;
  background: #ef4444;
  border: none;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.25);
}

.btn-danger:hover {
  background: #dc2626;
  box-shadow: 0 6px 18px rgba(239, 68, 68, 0.35);
}

/* 滚动条美化 */
.activity-table :deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
}

.activity-table :deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #dce1e8;
  border-radius: 10px;
}

/* ==================== 响应式 ==================== */
@media (max-width: 992px) {
  .activity-page {
    padding: 16px;
    gap: 14px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 768px) {
  .activity-page {
    padding: 10px;
    gap: 10px;
  }

  .page-title {
    font-size: 20px;
  }

  .table-card {
    padding: 14px 8px 0;
  }

  .activity-dialog {
    width: 92% !important;
  }

  .delete-dialog {
    width: 80% !important;
  }
}
</style>