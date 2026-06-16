<template>
  <div class="order-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-line">
          <span class="title-icon">📋</span>
          <h1 class="page-title">订单管理</h1>
        </div>
        <p class="page-subtitle">查看所有订单详情，支持按订单状态筛选与分页浏览</p>
      </div>
      <div class="header-stats">
        <div class="stat-card stat--total">
          <div class="stat-icon">
            <el-icon>
              <Document />
            </el-icon>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ totalCount }}</span>
            <span class="stat-label">订单总数</span>
          </div>
        </div>
        <div class="stat-card stat--success">
          <div class="stat-icon">
            <el-icon>
              <CircleCheck />
            </el-icon>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ paidCount }}</span>
            <span class="stat-label">支付成功</span>
          </div>
        </div>
        <div class="stat-card stat--pending">
          <div class="stat-icon">
            <el-icon>
              <Clock />
            </el-icon>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ pendingCount }}</span>
            <span class="stat-label">待处理</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card" v-loading="loading" element-loading-text="正在加载订单数据...">
      <el-table :data="orderList" class="order-table" stripe row-key="order.id" :header-cell-style="headerCellStyle"
        :row-style="{ height: '76px' }" highlight-current-row>
        <!-- 展开行：订单详情网格 -->
        <el-table-column type="expand" width="48">
          <template #default="props">
            <div class="expand-panel">
              <div class="expand-header">
                <span class="expand-badge">订单详情</span>
              </div>
              <div class="expand-grid">
                <div class="expand-item">
                  <span class="expand-label">订单 ID</span>
                  <span class="expand-value">{{ formatCell(props.row.order?.id) }}</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">用户 ID</span>
                  <span class="expand-value">{{ formatCell(props.row.user?.id) }}</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">电影 ID</span>
                  <span class="expand-value">{{ formatCell(props.row.film?.id) }}</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">场次 ID</span>
                  <span class="expand-value">{{ formatCell(props.row.arrangement?.id) }}</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">电影名称</span>
                  <span class="expand-value">《{{ formatCell(props.row.film?.name) }}》</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">座位号</span>
                  <span class="expand-value">{{ formatCell(props.row.order?.seats) }}</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">订单金额</span>
                  <span class="expand-value price-value">{{ moneyText(props.row.order?.price) }}</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">下单时间</span>
                  <span class="expand-value">{{ formatCell(props.row.order?.createAt) }}</span>
                </div>
                <div class="expand-item">
                  <span class="expand-label">支付时间</span>
                  <span class="expand-value">{{ formatCell(props.row.order?.payAt) }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 订单 ID -->
        <el-table-column label="订单 ID" min-width="220" show-overflow-tooltip prop="order.id" align="left" />

        <!-- 电影名 -->
        <el-table-column label="电影名" min-width="200" prop="film.name" align="center">
          <template #default="scope">
            <span class="film-name">{{ formatCell(scope.row.film?.name) }}</span>
          </template>
        </el-table-column>

        <!-- 订购座位 -->
        <el-table-column label="订购座位" min-width="180" prop="order.seats" align="center">
          <template #default="scope">
            <span class="seat-text">{{ formatCell(scope.row.order?.seats) }}</span>
          </template>
        </el-table-column>

        <!-- 订单金额 -->
        <el-table-column label="订单金额" min-width="140" align="center">
          <template #default="scope">
            <span class="table-money">{{ moneyText(scope.row.order?.price) }}</span>
          </template>
        </el-table-column>

        <!-- 订单状态 -->
        <el-table-column label="订单状态" min-width="150" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.order.status === 2" type="success" effect="light" class="status-tag">
              支付成功
            </el-tag>
            <el-tag v-else-if="scope.row.order.status === 0" type="info" effect="light" class="status-tag">
              等待支付
            </el-tag>
            <el-tag v-else-if="scope.row.order.status === 3" type="warning" effect="light" class="status-tag">
              已被撤销
            </el-tag>
            <el-tag v-else-if="scope.row.order.status === 1" type="danger" effect="light" class="status-tag">
              支付失败
            </el-tag>
            <el-tag v-else-if="scope.row.order.status === 4" type="info" effect="light" class="status-tag">
              已退款
            </el-tag>
            <span v-else class="unknown-status">{{ formatCell(scope.row.order.status) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" background />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, CircleCheck, Clock } from '@element-plus/icons-vue'
import { listOrderPage } from '@/api/order'

// ==================== 响应式状态 ====================
const loading = ref(false)
const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)

// ==================== 计算属性 ====================
const paidCount = computed(() =>
  orderList.value.filter(item => item.order?.status === 2).length
)

const pendingCount = computed(() =>
  orderList.value.filter(item => item.order?.status === 0 || item.order?.status === 1).length
)

// 表头样式常量
const headerCellStyle = {
  background: 'linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%)',
  color: '#1e293b',
  fontWeight: 700,
  fontSize: '14px',
  textAlign: 'center',
  height: '56px',
  borderBottom: '2px solid #e2e8f0'
}

// ==================== 工具方法 ====================
function formatCell(val) {
  if (val == null) return '—'
  const s = String(val).trim()
  if (s === '' || s === 'null') return '—'
  return s
}

function moneyText(price) {
  if (price == null || price === '') return '—'
  const n = Number(price)
  if (isNaN(n)) return String(price)
  return `¥${n.toFixed(2)}`
}

// ==================== 数据加载 ====================
async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    const res = await listOrderPage(params)
    if (!res?.success) return
    orderList.value = res.data.rows || []
    totalCount.value = res.data.total || 0
    currentPage.value = res.data.page || currentPage.value
  } catch (err) {
    console.error('加载订单失败', err)
    ElMessage.error('订单数据加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function handleSizeChange(newSize) {
  pageSize.value = newSize
  currentPage.value = 1
  loadOrders()
}

function handleCurrentChange(newPage) {
  currentPage.value = newPage
  loadOrders()
}

// ==================== 生命周期 ====================
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
/* ==================== 全局页面容器 ==================== */
.order-page {
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

/* 头部统计卡片 */
.header-stats {
  display: flex;
  gap: 12px;
}

.stat-card {
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

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.stat--total .stat-icon {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  color: #3b82f6;
}

.stat--success .stat-icon {
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  color: #10b981;
}

.stat--pending .stat-icon {
  background: linear-gradient(135deg, #fffbeb, #fef3c7);
  color: #f59e0b;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
}

.stat-label {
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

.order-table {
  flex: 1;
  min-height: 0;
  border-radius: 16px;
}

/* 表格细节 */
.order-table :deep(.el-table__header-wrapper th) {
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.order-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(241, 245, 249, 0.7) !important;
}

.order-table :deep(.el-table__row--striped td) {
  background-color: #fafbfd;
}

.order-table :deep(.el-table__cell .cell) {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 第一列（订单ID）左对齐 */
.order-table :deep(.el-table__cell:first-child .cell) {
  justify-content: flex-start;
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
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 20px;
}

.expand-item {
  display: flex;
  align-items: baseline;
  font-size: 13px;
  border-bottom: 1px dashed #edf2f7;
  padding-bottom: 8px;
}

.expand-label {
  width: 90px;
  font-weight: 600;
  color: #64748b;
  flex-shrink: 0;
}

.expand-value {
  flex: 1;
  color: #1e293b;
  word-break: break-word;
}

.price-value {
  font-weight: 700;
  color: #ef4444;
}

/* 表格内文本样式 */
.film-name {
  font-weight: 500;
  color: #1e293b;
}

.seat-text {
  color: #475569;
  font-family: 'SF Mono', 'Fira Code', monospace;
}

.table-money {
  font-weight: 700;
  color: #ef4444;
  font-size: 15px;
}

/* 状态标签 */
.status-tag {
  border-radius: 20px;
  padding: 4px 14px;
  font-weight: 600;
  border: 1px solid transparent !important;
}

.status-tag.el-tag--success {
  background-color: #ecfdf5 !important;
  color: #059669 !important;
  border-color: #a7f3d0 !important;
}

.status-tag.el-tag--info {
  background-color: #f1f5f9 !important;
  color: #475569 !important;
  border-color: #cbd5e1 !important;
}

.status-tag.el-tag--warning {
  background-color: #fffbeb !important;
  color: #d97706 !important;
  border-color: #fcd34d !important;
}

.status-tag.el-tag--danger {
  background-color: #fef2f2 !important;
  color: #dc2626 !important;
  border-color: #fecaca !important;
}

.unknown-status {
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

.pagination-container :deep(.btn-prev),
.pagination-container :deep(.btn-next) {
  border-radius: 10px;
  background: #fff;
  border: 1px solid #e2e8f0;
}

.pagination-container :deep(.btn-prev:hover),
.pagination-container :deep(.btn-next:hover) {
  background: #f1f5f9;
  border-color: #94a3b8;
  color: #1e293b;
}

/* 滚动条美化 */
.order-table :deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
}

.order-table :deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: transparent;
}

.order-table :deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #dce1e8;
  border-radius: 10px;
}

.order-table :deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #bcc5d0;
}

/* ==================== 响应式适配 ==================== */
@media (max-width: 992px) {
  .order-page {
    padding: 16px;
    gap: 14px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-stats {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .page-title {
    font-size: 22px;
  }
}

@media (max-width: 768px) {
  .order-page {
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

  .header-stats {
    gap: 8px;
  }

  .stat-card {
    padding: 8px 12px;
  }

  .stat-value {
    font-size: 18px;
  }

  .table-card {
    padding: 14px 8px 0;
    border-radius: 18px;
  }

  .expand-grid {
    grid-template-columns: 1fr;
  }

  .status-tag {
    font-size: 12px;
    padding: 3px 10px;
  }
}
</style>