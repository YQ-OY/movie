<template>
  <div class="film-list">

    <!-- 页面头部（与电影管理页面完全一致） -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">订单管理</div>
          <div class="page-subtitle">查看所有订单详情，支持按订单状态筛选</div>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="orderList" class="film-table" stripe row-key="order.id"
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <!-- 展开行（详情网格） -->
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

        <el-table-column label="订单 ID" min-width="220" show-overflow-tooltip prop="order.id" align="left" />
        <el-table-column label="电影名" min-width="200" prop="film.name" align="center" />
        <el-table-column label="订购座位" min-width="180" prop="order.seats" align="center" />
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
            <span v-else>{{ formatCell(scope.row.order.status) }}</span>
          </template>
        </el-table-column>
      </el-table>
  <div class="main page-list">

    <el-table
        v-loading="loading"
        :data="orders"
        style="width: 100%">
      <el-table-column type="expand" width="48">
        <template #default="props">
          <div class="expand-detail">
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
                <span class="expand-detail__value expand-detail__value--money">{{ moneyText(props.row.order?.price) }}</span>
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
        </template>
      </el-table-column>
      <el-table-column
          label="订单 ID"
          min-width="220"
          show-overflow-tooltip
          prop="order.id">
      </el-table-column>
      <el-table-column
          width="200"
          label="电影名"
          prop="film.name">
      </el-table-column>
      <el-table-column
          width="200"
          label="订购座位"
          prop="order.seats">
      </el-table-column>
      <el-table-column
          width="200"
          label="订单金额"
          prop="order.price">
        <template #default="scope">
          <span class="table-money">{{ moneyText(scope.row.order?.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态">
        <template #default="scope">
          <el-tag v-if="scope.row.order.status === 2" type="success">支付成功</el-tag>
          <el-tag v-if="scope.row.order.status === 0" type="info">等待支付</el-tag>
          <el-tag v-if="scope.row.order.status === 3" type="warning">已被撤销</el-tag>
          <el-tag v-if="scope.row.order.status === 1" type="danger">支付失败</el-tag>
          <el-tag v-if="scope.row.order.status === 4" type="info">已退款</el-tag>
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
  </div>
</template>

<script>
import { listOrderPage } from "@/api/order"; // 假设已实现分页接口

export default {
  name: 'OrderManage',
  data() {
    return {
      loading: false,
      orderList: [],    // 当前页数据
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
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
        }
        const res = await listOrderPage(params)  // 后端分页接口
        if (res.success) {
          this.orderList = res.data.rows || []
          this.totalCount = res.data.total || 0
          this.currentPage = res.data.page || this.currentPage
        } else {
          this.$message.error(res.msg || '加载失败')
        }
      } catch (error) {
        this.$message.error('加载订单列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
      this.loadOrders()
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadOrders()
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

/* 表格卡片 */
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

/* 展开行内部样式（网格详情） */
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
</style>