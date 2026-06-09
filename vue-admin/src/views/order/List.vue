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
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button @click="handle1(scope.$index, scope.row.order)" size="small" type="warning"
                     :icon="RefreshRight"
                     :disabled="scope.row.order.status === 3"
                     plain>撤销订单
          </el-button>
          <el-button @click="handle2(scope.$index, scope.row.order)" size="small" type="danger"
                     :icon="CircleClose"
                     plain>上报异常
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="上报异常表单" v-model="dialogFormVisible">
      <el-form :model="form" style="width: 400px">
        <el-form-item style="width: 100%" label="上报人" label-width="120px">
          <el-input v-model="form.reason" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item style="width: 100%" label="上报原因" label-width="120px">
          <el-input v-model="form.reviewer" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="submit">确 定</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import {CreateOrderException, FindAllOrder, UpdateOrder} from "@/api/order";
import { RefreshRight, CircleClose } from '@element-plus/icons-vue'

export default {
  components: { RefreshRight, CircleClose },

  data() {
    return {
      loading: false,
      dialogFormVisible: false,
      orders: [],
      form: {
        oid: '',
        reason: '',
        reviewer: '',
      },
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

    handle1(index, order) {
      this.orders[index].order.status = 3
      UpdateOrder(this.orders[index].order).then(res => {
        console.log(res)
        this.loadOrderList();
        this.$message({
          type: 'success',
          message: '订单撤销成功!'
        });
      })
    },

    handle2(index, order) {
      this.form.oid = order.id
      this.dialogFormVisible = true
    },

    submit() {
      CreateOrderException(this.form).then(res => {
        if (res.success) {
          this.$message({
            type: 'success',
            message: '订单异常上报成功!'
          });
          this.dialogFormVisible = false
        }
      })
    }

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
