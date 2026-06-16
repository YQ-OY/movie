<template>
  <div class="film-list">

    <!-- 搜索区 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">订单管理</div>
          <div class="page-subtitle">支持按订单ID / 用户ID搜索，管理订单状态与异常上报</div>
        </div>

        <div class="search-form">
          <el-input v-model="searchKeyword" clearable placeholder="订单ID / 用户ID" class="search-item search-item--name"
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
      <el-table v-loading="loading" :data="orderList" class="film-table" stripe row-key="order.id"
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <!-- 展开行 -->
        <el-table-column type="expand" width="48">
          <template #default="props">
            <div class="expand-detail expand-detail--with-aside">
              <div class="expand-detail__body">
                <div class="expand-detail__grid">
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">订单 ID</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.order?.id) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">用户 ID</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.user?.id) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">电影 ID</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.film?.id) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">场次 ID</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.arrangement?.id) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">电影名称</span>
                    <span class="expand-detail__value">《{{ formatCell(props.row.film?.name) }}》</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">座位号</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.order?.seats) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">订单金额</span>
                    <span class="expand-detail__value expand-detail__value--money">{{ moneyText(props.row.order?.price)
                    }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">下单时间</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.order?.createAt) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">支付时间</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.order?.payAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="订单 ID" min-width="220" show-overflow-tooltip prop="order.id" />
        <el-table-column label="订单金额" min-width="140" align="center">
          <template #default="scope">
            <span class="table-money">{{ moneyText(scope.row.order?.price) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" min-width="140" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.order.status === 2" type="success" effect="light">支付成功</el-tag>
            <el-tag v-else-if="scope.row.order.status === 0" type="info" effect="light">等待支付</el-tag>
            <el-tag v-else-if="scope.row.order.status === 3" type="warning" effect="light">已被撤销</el-tag>
            <el-tag v-else-if="scope.row.order.status === 1" type="danger" effect="light">支付失败</el-tag>
            <el-tag v-else-if="scope.row.order.status === 4" type="info" effect="plain">已退款</el-tag>
            <span v-else>{{ formatCell(scope.row.order.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="warning" plain :icon="RefreshRight"
                :disabled="scope.row.order.status === 3 || scope.row.order.status === 4"
                @click="handleRevokeOrder(scope.row)">
                撤销订单
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

    <el-dialog v-model="refundDialogVisible" title="确认退款" width="480px" align-center :close-on-click-modal="false"
      @closed="resetRefundDialog">
      <el-alert title="将通过支付宝沙箱原路退款，退款成功后订单状态变为「已退款」，座位将释放。" type="warning" :closable="false" show-icon
        style="margin-bottom: 16px" />
      <div v-if="refundTarget" class="refund-dialog__info">
        <p><span>订单 ID</span>{{ refundTarget.order?.id }}</p>
        <p><span>电影</span>《{{ formatCell(refundTarget.film?.name) }}》</p>
        <p><span>座位</span>{{ formatCell(refundTarget.order?.seats) }}</p>
        <p><span>放映</span>{{ formatCell(refundTarget.arrangement?.date) }} {{
          formatCell(refundTarget.arrangement?.startTime) }}</p>
        <p><span>用户 ID</span>{{ formatCell(refundTarget.user?.id) }}</p>
        <p><span>支付时间</span>{{ formatCell(refundTarget.order?.payAt) }}</p>
        <p><span>退款金额</span><em class="refund-dialog__price">{{ moneyText(refundTarget.order?.price) }}</em></p>
      </div>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="warning" :loading="refundSubmitting" @click="confirmAdminRefund">确定退款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listOrderPage, UpdateOrder, AdminRefundOrder } from "@/api/order";
import { RefreshRight, CircleClose, Search, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'OrderManage',
  components: { RefreshRight, CircleClose, Search, Refresh },
  data() {
    return {
      loading: false,
      orderList: [],          // 当前页数据
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      dialogFormVisible: false,
      exceptionForm: {
        oid: '',
        reviewer: '',
        reason: '',
      },
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

    handleReportException(index, order) {
      this.exceptionForm = {
        oid: order.id,
        reviewer: '',
        reason: '',
      }
      this.dialogFormVisible = true
    },
  }
}
</script>

<style scoped>
/* ===== 完全复用电影管理页面的全局样式 ===== */
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

/* 表头样式 */
.film-table :deep(.el-table__header-wrapper th) {
  border-bottom: 1px solid #e2e8f0;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.2px;
  text-align: center;
  height: 58px;
}

/* 单元格居中（第一列展开列除外） */
.film-table :deep(.cell) {
  text-align: center;
}

.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: left;
}

.film-table :deep(.el-table__expanded-cell .cell) {
  text-align: left;
}

/* 行悬浮效果 */
.film-table :deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

.film-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(148, 163, 184, 0.14) !important;
}

/* 展开行内部样式 */
.expand-detail--with-aside {
  padding: 16px 24px;
  background: #fbfdff;
  border-radius: 16px;
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
  width: 90px;
  font-weight: 600;
  color: #64748b;
  flex-shrink: 0;
}

.expand-detail__value {
  flex: 1;
  color: #1e293b;
  word-break: break-word;
}

.expand-detail__value--money {
  font-weight: 700;
  color: #ef4444;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-buttons .el-button {
  border-radius: 10px;
  min-width: 92px;
  height: 34px;
  font-size: 13px;
}

/* 金额展示 */
.table-money {
  font-weight: 700;
  color: #ef4444;
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

/* 对话框样式 */
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

/* 按钮图标与文字对齐 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.refund-dialog__info {
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  line-height: 2;
  color: #475569;
}

.refund-dialog__info p {
  margin: 0;
}

.refund-dialog__info span {
  display: inline-block;
  width: 72px;
  color: #94a3b8;
}

.refund-dialog__price {
  font-style: normal;
  font-size: 18px;
  font-weight: 700;
  color: #e6a23c;
}
</style>