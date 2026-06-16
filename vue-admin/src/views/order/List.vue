<template>
  <div class="film-list">
    <!-- 搜索区 - 统一风格 -->
    <div class="page-header">
      <div class="search-container">
        <!-- 左侧：搜索筛选区域 -->
        <div class="search-filters">
          <el-input v-model="searchKeyword" clearable placeholder="订单ID / 用户ID" class="search-item search-item--name"
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
          <el-button class="btn-secondary btn-export" @click="exportOrders">
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
      <el-table v-loading="loading" :data="orderList" class="film-table" stripe row-key="order.id"
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="订单 ID" min-width="220" show-overflow-tooltip prop="order.id" />
        <el-table-column label="订单金额" min-width="140" align="center">
          <template #default="scope">
            <span class="table-money">{{ moneyText(scope.row.order?.price) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" min-width="140" align="center">
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.order?.status)" effect="light" :disable-transitions="true">
              {{ getOrderStatusText(scope.row.order?.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="openDetailDialog(scope.row)">
                <el-icon>
                  <View />
                </el-icon>
                <span>详情</span>
              </el-button>
              <el-button size="small" type="warning" plain @click="handleRevokeOrder(scope.row)"
                :disabled="scope.row.order?.status === 3 || scope.row.order?.status === 4">
                <el-icon>
                  <RefreshRight />
                </el-icon>
                <span>撤销订单</span>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" :pager-count="5" :disabled="loading"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" v-model="detailDialogVisible" width="70%" align-center
      class="film-dialog film-dialog--detail">
      <div class="detail-layout">
        <div class="detail-body full-width">
          <div class="info-card">
            <div class="info-card__header">
              <span class="info-card__title">订单详细信息</span>
              <el-tag :type="getOrderStatusType(currentOrder.order?.status)" effect="light" class="info-card__status">
                {{ getOrderStatusText(currentOrder.order?.status) }}
              </el-tag>
            </div>
            <div class="info-card__grid">
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Document />
                  </el-icon>
                  <span>订单ID</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentOrder.order?.id) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <User />
                  </el-icon>
                  <span>用户ID</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentOrder.user?.id) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Film />
                  </el-icon>
                  <span>电影ID</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentOrder.film?.id) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <VideoPlay />
                  </el-icon>
                  <span>场次ID</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentOrder.arrangement?.id) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Film />
                  </el-icon>
                  <span>电影名称</span>
                </div>
                <div class="info-item__value">《{{ formatCell(currentOrder.film?.name) }}》</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Tickets />
                  </el-icon>
                  <span>座位号</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentOrder.order?.seats) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Money />
                  </el-icon>
                  <span>订单金额</span>
                </div>
                <div class="info-item__value info-item__value--money">{{ moneyText(currentOrder.order?.price) }}</div>
              </div>
              <div class="info-item">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <Clock />
                  </el-icon>
                  <span>下单时间</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentOrder.order?.createAt) }}</div>
              </div>
              <div class="info-item info-item--full">
                <div class="info-item__label">
                  <el-icon class="info-item__icon">
                    <SuccessFilled />
                  </el-icon>
                  <span>支付时间</span>
                </div>
                <div class="info-item__value">{{ formatCell(currentOrder.order?.payAt) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="warning" @click="handleRevokeOrder(currentOrder)"
            :disabled="currentOrder.order?.status === 3 || currentOrder.order?.status === 4">
            <el-icon>
              <RefreshRight />
            </el-icon>
            撤销订单
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 退款确认对话框 -->
    <el-dialog v-model="refundDialogVisible" title="确认退款" width="520px" align-center :close-on-click-modal="false"
      @closed="resetRefundDialog" class="film-dialog film-dialog--refund">
      <el-alert title="将通过支付宝沙箱原路退款，退款成功后订单状态变为「已退款」，座位将自动释放。" type="warning" :closable="false" show-icon
        style="margin-bottom: 20px" />

      <div class="refund-info-card">
        <div class="refund-info-card__header">
          <span class="refund-info-card__title">退款详情</span>
        </div>
        <div class="refund-info-card__grid">
          <div class="refund-info-item">
            <span class="refund-info-item__label">订单ID</span>
            <span class="refund-info-item__value">{{ formatCell(refundTarget?.order?.id) }}</span>
          </div>
          <div class="refund-info-item">
            <span class="refund-info-item__label">电影名称</span>
            <span class="refund-info-item__value">《{{ formatCell(refundTarget?.film?.name) }}》</span>
          </div>
          <div class="refund-info-item">
            <span class="refund-info-item__label">座位号</span>
            <span class="refund-info-item__value">{{ formatCell(refundTarget?.order?.seats) }}</span>
          </div>
          <div class="refund-info-item">
            <span class="refund-info-item__label">放映时间</span>
            <span class="refund-info-item__value">{{ formatCell(refundTarget?.arrangement?.date) }} {{
              formatCell(refundTarget?.arrangement?.startTime) }}</span>
          </div>
          <div class="refund-info-item">
            <span class="refund-info-item__label">用户ID</span>
            <span class="refund-info-item__value">{{ formatCell(refundTarget?.user?.id) }}</span>
          </div>
          <div class="refund-info-item">
            <span class="refund-info-item__label">支付时间</span>
            <span class="refund-info-item__value">{{ formatCell(refundTarget?.order?.payAt) }}</span>
          </div>
          <div class="refund-info-item refund-info-item--full">
            <span class="refund-info-item__label">退款金额</span>
            <span class="refund-info-item__value refund-info-item__value--price">{{
              moneyText(refundTarget?.order?.price)
              }}</span>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="refundDialogVisible = false">取消</el-button>
          <el-button type="warning" :loading="refundSubmitting" @click="confirmAdminRefund">
            确定退款
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listOrderPage, UpdateOrder, AdminRefundOrder } from "@/api/order";
import {
  RefreshRight, Search, Refresh, Download, Document,
  User, Film, VideoPlay, Tickets, Money, Clock, SuccessFilled, View
} from '@element-plus/icons-vue'
import { downloadCsv } from '@/utils/exportCsv'

export default {
  name: 'OrderManage',
  components: {
    RefreshRight, Search, Refresh, Download, Document,
    User, Film, VideoPlay, Tickets, Money, Clock, SuccessFilled, View
  },
  data() {
    return {
      loading: false,
      orderList: [],          // 当前页数据
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      detailDialogVisible: false, // 详情对话框开关
      currentOrder: {}, // 当前查看的订单信息
      refundDialogVisible: false,
      refundSubmitting: false,
      refundTarget: null,
    }
  },
  mounted() {
    this.loadOrders()
  },
  methods: {
    formatCell(val) {
      if (val == null) return '—'
      const s = String(val).trim()
      if (s === '' || s === 'null') return '—'
      return s
    },
    moneyText(price) {
      if (price == null || price === '') return '—'
      const n = Number(price)
      if (isNaN(n)) return String(price)
      return `¥${n.toFixed(2)}`
    },
    // 获取订单状态类型
    getOrderStatusType(status) {
      switch (status) {
        case 0: return 'info'
        case 1: return 'danger'
        case 2: return 'success'
        case 3: return 'warning'
        case 4: return 'info'
        default: return 'info'
      }
    },
    // 获取订单状态文本
    getOrderStatusText(status) {
      switch (status) {
        case 0: return '等待支付'
        case 1: return '支付失败'
        case 2: return '支付成功'
        case 3: return '已被撤销'
        case 4: return '已退款'
        default: return String(status)
      }
    },
    exportOrders() {
      const rows = (this.orderList || []).map((item) => ([
        item.order?.id ?? '—',
        item.user?.id ?? '—',
        item.film?.name ?? '—',
        item.arrangement?.id ?? '—',
        item.order?.seats ?? '—',
        item.order?.price ?? '—',
        this.getOrderStatusText(item.order?.status),
        item.order?.createAt ?? '—',
        item.order?.payAt ?? '—',
      ]))
      downloadCsv(
        `订单列表_${new Date().toISOString().slice(0, 10)}.csv`,
        ['订单ID', '用户ID', '电影名称', '场次ID', '座位号', '金额', '状态', '下单时间', '支付时间'],
        rows
      )
    },
    async loadOrders() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword?.trim() || undefined,
        }
        const res = await listOrderPage(params)
        if (!res.success) return
        const total = Number(res.data?.total) || 0
        const rows = res.data?.rows || []
        const backendPage = Number(res.data?.page) || this.currentPage
        const backendTotalPages = Number(res.data?.totalPages) || (total ? Math.ceil(total / this.pageSize) : 0)

        if (total > 0 && backendPage > backendTotalPages) {
          this.currentPage = backendTotalPages
          await this.loadOrders()
          return
        }

        this.orderList = rows
        this.totalCount = total
        this.currentPage = backendPage

        if (total > 0 && rows.length === 0 && this.currentPage > 1) {
          this.currentPage -= 1
          await this.loadOrders()
        }
      } catch {
        // 全局 request 拦截器已提示
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadOrders()
    },
    handleResetSearch() {
      this.searchKeyword = ''
      this.currentPage = 1
      this.loadOrders()
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
      this.loadOrders()
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadOrders()
    },
    async handleRevokeOrder(row) {
      // 如果是从详情对话框点击的撤销，先关闭详情对话框
      if (this.detailDialogVisible) {
        this.detailDialogVisible = false
      }

      const order = row.order
      if (!order) return

      if (order.status === 2) {
        try {
          await this.$confirm(
            '确定要撤销该订单吗？支付成功的订单撤销后将进入退款流程。',
            '撤销订单',
            { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
          )
          this.refundTarget = row
          this.refundDialogVisible = true
        } catch {
          // 用户取消
        }
        return
      }

      try {
        await this.$confirm('确定要撤销该订单吗？', '撤销订单', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        const updatedOrder = { ...order, status: 3 }
        const res = await UpdateOrder(updatedOrder)
        if (!res?.success) return
        this.$message.success('订单撤销成功')
        this.loadOrders()
      } catch (e) {
        if (e !== 'cancel' && e !== 'close') {
          // 全局 request 拦截器已提示
        }
      }
    },
    resetRefundDialog() {
      this.refundTarget = null
      this.refundSubmitting = false
    },
    async confirmAdminRefund() {
      if (!this.refundTarget?.order?.id) return
      this.refundSubmitting = true
      try {
        const res = await AdminRefundOrder(this.refundTarget.order.id)
        if (!res?.success) return
        this.$message.success('退款成功')
        this.refundDialogVisible = false
        this.loadOrders()
      } catch {
        // 全局 request 拦截器已提示
      } finally {
        this.refundSubmitting = false
      }
    },
    // 打开订单详情对话框
    openDetailDialog(order) {
      this.currentOrder = { ...order }
      this.detailDialogVisible = true
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

/* 金额展示 */
.table-money {
  font-weight: 700;
  color: #ef4444;
  font-size: 15px;
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

/* 详情对话框布局 */
.detail-layout {
  display: flex;
  gap: 24px;
}

.detail-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.detail-body.full-width {
  flex: 1 1 100%;
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

.info-item__value--money {
  font-weight: 700;
  color: #ef4444;
}

/* 退款信息卡片 */
.refund-info-card {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.refund-info-card__header {
  padding: 12px 16px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-bottom: 1px solid #fcd34d;
}

.refund-info-card__title {
  font-size: 14px;
  font-weight: 700;
  color: #92400e;
}

.refund-info-card__grid {
  padding: 16px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 16px;
}

.refund-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.refund-info-item--full {
  grid-column: 1 / -1;
}

.refund-info-item__label {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}

.refund-info-item__value {
  font-size: 14px;
  color: #1e293b;
  word-break: break-word;
}

.refund-info-item__value--price {
  font-size: 20px;
  font-weight: 700;
  color: #d97706;
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

  .info-card__grid {
    grid-template-columns: 1fr;
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

  .refund-info-card__grid {
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
}
</style>