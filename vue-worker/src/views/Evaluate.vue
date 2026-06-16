<template>
  <div class="evaluate-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-line">
          <span class="title-icon">⭐</span>
          <h1 class="page-title">我的评价</h1>
        </div>
        <p class="page-subtitle">查看用户对客服工作的满意度反馈</p>
      </div>
      <!-- 评价统计卡片 -->
      <div class="rating-stats">
        <div class="rating-card rating-card--total">
          <div class="rating-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round">
              <path d="M9 11l3 3L22 4" />
              <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
            </svg>
          </div>
          <div class="rating-card__body">
            <span class="rating-card__value">{{ totalCount }}</span>
            <span class="rating-card__label">评价总数</span>
          </div>
        </div>
        <div class="rating-card rating-card--happy">
          <div class="rating-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round">
              <circle cx="12" cy="12" r="10" />
              <path d="M8 14s1.5 2 4 2 4-2 4-2" />
              <line x1="9" y1="9" x2="9.01" y2="9" />
              <line x1="15" y1="9" x2="15.01" y2="9" />
            </svg>
          </div>
          <div class="rating-card__body">
            <span class="rating-card__value">{{ veryHappyCount }}</span>
            <span class="rating-card__label">非常满意</span>
          </div>
        </div>
        <div class="rating-card rating-card--sad">
          <div class="rating-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round">
              <circle cx="12" cy="12" r="10" />
              <path d="M8 15s1.5-2 4-2 4 2 4 2" />
              <line x1="9" y1="9" x2="9.01" y2="9" />
              <line x1="15" y1="9" x2="15.01" y2="9" />
            </svg>
          </div>
          <div class="rating-card__body">
            <span class="rating-card__value">{{ unhappyCount }}</span>
            <span class="rating-card__label">不满意</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card" v-loading="loading" element-loading-text="正在加载评价数据...">
      <el-table :data="paginatedList" class="evaluate-table" stripe :header-cell-style="headerCellStyle"
        :row-style="{ height: '76px' }" highlight-current-row empty-text="暂无评价记录">
        <!-- 评价日期 -->
        <el-table-column label="评价日期" min-width="180" prop="createAt" align="center">
          <template #default="scope">
            <div class="date-cell">
              <el-icon class="date-icon">
                <Calendar />
              </el-icon>
              <span>{{ formatDate(scope.row.createAt) }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 评语 -->
        <el-table-column label="评语" min-width="320" show-overflow-tooltip align="center">
          <template #default="scope">
            <span class="comment-text">{{ scope.row.content || '暂无评语' }}</span>
          </template>
        </el-table-column>

        <!-- 评价类型 -->
        <el-table-column label="评价类型" min-width="150" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.type === '非常满意'" type="success" effect="light" class="type-tag">
              <el-icon class="tag-icon">
                <CircleCheck />
              </el-icon> 非常满意
            </el-tag>
            <el-tag v-else-if="scope.row.type === '满意'" type="info" effect="light" class="type-tag">
              <el-icon class="tag-icon">
                <SemiSelect />
              </el-icon> 满意
            </el-tag>
            <el-tag v-else-if="scope.row.type === '不满意'" type="danger" effect="light" class="type-tag">
              <el-icon class="tag-icon">
                <CircleClose />
              </el-icon> 不满意
            </el-tag>
            <span v-else class="unknown-type">{{ scope.row.type || '—' }}</span>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, CircleCheck, SemiSelect, CircleClose } from '@element-plus/icons-vue'
import { ListWorkerEvaluate } from '@/api/leaving'

// ==================== 状态 ====================
const loading = ref(false)
const evaluateList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// ==================== 计算属性 ====================
const totalCount = computed(() => evaluateList.value.length)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return evaluateList.value.slice(start, start + pageSize.value)
})

const veryHappyCount = computed(() =>
  evaluateList.value.filter(item => item.type === '非常满意').length
)

const unhappyCount = computed(() =>
  evaluateList.value.filter(item => item.type === '不满意').length
)

const headerCellStyle = {
  background: 'linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%)',
  color: '#1e293b',
  fontWeight: 700,
  fontSize: '14px',
  textAlign: 'center',
  height: '56px',
  borderBottom: '2px solid #e2e8f0'
}

// ==================== 方法 ====================
function formatDate(dateStr) {
  if (!dateStr) return '—'
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) return dateStr
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch {
    return dateStr
  }
}

function loadList() {
  loading.value = true
  const wid = localStorage.getItem('wid')
  ListWorkerEvaluate(wid)
    .then(res => {
      if (!res?.success) return
      evaluateList.value = res.data || []
      currentPage.value = 1
    })
    .catch(() => {
      ElMessage.error('评价数据加载失败')
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

// ==================== 生命周期 ====================
onMounted(() => {
  loadList()
})
</script>

<style scoped>
/* ==================== 全局页面容器 ==================== */
.evaluate-page {
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
  padding: 20px 28px;
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
  filter: drop-shadow(0 4px 6px rgba(245, 158, 11, 0.3));
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

/* 评价统计卡片 */
.rating-stats {
  display: flex;
  gap: 12px;
}

.rating-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 10px 18px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.rating-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.rating-card__icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.rating-card__icon svg {
  width: 20px;
  height: 20px;
}

.rating-card--total .rating-card__icon {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  color: #3b82f6;
}

.rating-card--happy .rating-card__icon {
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  color: #10b981;
}

.rating-card--sad .rating-card__icon {
  background: linear-gradient(135deg, #fef2f2, #fecaca);
  color: #ef4444;
}

.rating-card__body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.rating-card__value {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
}

.rating-card__label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
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

.evaluate-table {
  flex: 1;
  min-height: 0;
  border-radius: 16px;
}

/* 表格细节 */
.evaluate-table :deep(.el-table__header-wrapper th) {
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.evaluate-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(241, 245, 249, 0.7) !important;
}

.evaluate-table :deep(.el-table__row--striped td) {
  background-color: #fafbfd;
}

.evaluate-table :deep(.el-table__cell .cell) {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 评语文本 */
.comment-text {
  color: #334155;
  font-size: 14px;
  line-height: 1.5;
  text-align: left;
  display: block;
  max-width: 100%;
}

/* 评价类型标签 */
.type-tag {
  border-radius: 20px;
  padding: 4px 14px;
  font-weight: 600;
  border: 1px solid transparent !important;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.type-tag .tag-icon {
  font-size: 14px;
}

.type-tag.el-tag--success {
  background-color: #ecfdf5 !important;
  color: #059669 !important;
  border-color: #a7f3d0 !important;
}

.type-tag.el-tag--info {
  background-color: #f1f5f9 !important;
  color: #475569 !important;
  border-color: #cbd5e1 !important;
}

.type-tag.el-tag--danger {
  background-color: #fef2f2 !important;
  color: #dc2626 !important;
  border-color: #fecaca !important;
}

.unknown-type {
  color: #94a3b8;
  font-size: 13px;
}

/* ==================== 分页容器 ==================== */
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
  color: #334155;
  transition: all 0.2s;
}

.pagination-container :deep(.el-pager li:hover) {
  background: #f1f5f9;
  border-color: #94a3b8;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border: none;
  color: #fff;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.35);
}

/* 滚动条美化 */
.evaluate-table :deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
}

.evaluate-table :deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #dce1e8;
  border-radius: 10px;
}

.evaluate-table :deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #bcc5d0;
}

/* ==================== 响应式适配 ==================== */
@media (max-width: 992px) {
  .evaluate-page {
    padding: 16px;
    gap: 14px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .rating-stats {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .page-title {
    font-size: 22px;
  }
}

@media (max-width: 768px) {
  .evaluate-page {
    padding: 10px;
    gap: 10px;
  }

  .page-header {
    padding: 16px 20px;
  }

  .title-icon {
    font-size: 24px;
  }

  .page-title {
    font-size: 20px;
  }

  .rating-stats {
    gap: 8px;
  }

  .rating-card {
    padding: 8px 12px;
  }

  .rating-card__value {
    font-size: 18px;
  }

  .table-card {
    padding: 14px 8px 0;
    border-radius: 18px;
  }

  .type-tag {
    font-size: 12px;
    padding: 3px 10px;
  }
}
</style>