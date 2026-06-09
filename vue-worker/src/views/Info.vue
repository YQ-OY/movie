<template>
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

  </div>
</template>

<script>
import { FindAllOrder } from "@/api/order";

export default {

  data() {
    return {
      loading: false,
      orders: [],
    }
  },

  mounted() {
    this.loadOrderList()
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
      if (Number.isNaN(n)) return String(price)
      return `¥${n.toFixed(2)}`
    },

    loadOrderList() {
      this.loading = true
      FindAllOrder().then(res => {
        if (res.success) {
          setTimeout(() => {
            this.orders = res.data
            this.loading = false
          }, 700)
        }
      })
    },

  }

}
</script>

<style scoped>
.page-list {
  padding: 24px;
}

.table-money {
  font-weight: 600;
  color: var(--el-color-danger);
}
</style>
