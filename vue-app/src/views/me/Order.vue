<template>
  <div class="ticket-page">
    <h3 class="ticket-page__title">我的订单</h3>

    <div v-loading="loading" class="ticket-list">
      <div v-for="(item, index) in orderList" :key="index" class="ticket-card">
        <div class="ticket-card__main">
          <div class="ticket-card__poster">
            <img class="ticket-card__cover" alt="" :src="item.film.cover"/>
          </div>

          <div class="ticket-card__info">
            <div>
              <p class="info-text info-text--title">《{{ item.film.name }}》</p>
              <p :class="['info-text', statusTextClass(item.order.status)]">
                订单状态：{{ statusLabel(item.order.status) }}
              </p>
              <p class="info-text">订购座位：{{ item.order.seats }}</p>
              <p class="info-text info-text--accent">
                放映时间：{{ item.arrangement.date }} {{ item.arrangement.startTime }}
              </p>
              <p class="info-text">下单时间：{{ item.order.createAt }}</p>
              <p
                  v-if="item.order.status === 2 || item.order.status === 4"
                  class="info-text info-text--pay">
                支付时间：{{ item.order.payAt }}
              </p>
              <p class="info-text info-text--price-row">
                <span class="info-price">￥{{ formatPrice(item.order.price) }}</span>
              </p>
            </div>

            <div v-if="item.order.status === 0" class="info-action-row">
              <el-button
                  class="info-action-btn"
                  type="danger"
                  @click="handlePay(item.order)">
                去支付
              </el-button>
            </div>
            <div v-else-if="item.order.status === 2 && canRefund(item)" class="info-action-row">
              <el-button
                  class="info-action-btn"
                  type="warning"
                  plain
                  @click="openRefundDialog(item)">
                申请退款
              </el-button>
            </div>
            <div v-else-if="item.order.status === 2" class="info-action-row">
              <el-tooltip content="电影已开场或放映时间已过，无法退款" placement="top">
                <span class="info-action-hint">放映开始后不可退款</span>
              </el-tooltip>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && orderList.length === 0" class="ticket-empty">
        暂无订单
      </div>
    </div>

    <el-dialog
        v-model="refundDialogVisible"
        title="申请退款"
        width="440px"
        align-center
        :close-on-click-modal="false"
        @closed="resetRefundDialog"
    >
      <div v-if="refundTarget" class="refund-dialog">
        <el-alert
            title="退款将通过支付宝沙箱原路退回，成功后座位将立即释放。"
            type="warning"
            :closable="false"
            show-icon
        />
        <div class="refund-dialog__info">
          <p><span>电影</span>《{{ refundTarget.film.name }}》</p>
          <p><span>座位</span>{{ refundTarget.order.seats }}</p>
          <p><span>放映</span>{{ refundTarget.arrangement.date }} {{ refundTarget.arrangement.startTime }}</p>
          <p><span>金额</span><em class="refund-dialog__price">￥{{ refundTarget.order.price }}</em></p>
        </div>
      </div>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="warning" :loading="refundSubmitting" @click="confirmRefund">
          确认退款
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {FindOrderByUser, RefundOrder} from "@/api/order";
import { isBeforeShowStart } from "@/utils/ticketSale";

const STATUS_LABEL = {
  0: '等待支付',
  1: '订单超时',
  2: '支付成功',
  3: '订单异常',
  4: '已退款',
}

const STATUS_TEXT = {
  0: 'info-text--status-waiting',
  1: 'info-text--status-danger',
  2: 'info-text--status-success',
  3: 'info-text--status-warning',
  4: 'info-text--status-refunded',
}

export default {
  name: "Order",

  data() {
    return {
      loading: false,
      orderList: [],
      uid: localStorage.getItem("uid"),
      refundDialogVisible: false,
      refundSubmitting: false,
      refundTarget: null,
    }
  },

  mounted() {
    this.loadOrder()
  },

  methods: {

    formatPrice(price) {
      const n = Number(price)
      return Number.isNaN(n) ? price : n.toFixed(2).replace(/\.00$/, '')
    },

    statusLabel(status) {
      return STATUS_LABEL[status] || '未知状态'
    },

    statusTextClass(status) {
      return STATUS_TEXT[status] || 'info-text--status-waiting'
    },

    canRefund(item) {
      return isBeforeShowStart(item.arrangement?.date, item.arrangement?.startTime)
    },

    loadOrder() {
      this.loading = true
      FindOrderByUser(this.uid).then(res => {
        setTimeout(() => {
          this.orderList = res.data
          this.loading = false
        }, 700)
      })
    },

    handlePay(order) {
      this.$confirm(
          `请您仔细确认订单金额为 ${order.price} 元。请在 5 分钟内完成支付，超时订单将失效。`,
          '提示',
          {
            confirmButtonText: '确认支付',
            cancelButtonText: '取消支付',
            type: 'warning',
            center: true
          }
      ).then(() => {
        this.$router.push({ path: '/pay', query: { orderId: order.id } })
      }).catch(() => {
        this.$message.warning('用户已取消支付')
      });
    },

    openRefundDialog(item) {
      this.refundTarget = item
      this.refundDialogVisible = true
    },

    resetRefundDialog() {
      this.refundTarget = null
      this.refundSubmitting = false
    },

    confirmRefund() {
      if (!this.refundTarget) return
      const order = this.refundTarget.order
      this.refundSubmitting = true
      RefundOrder(order.id, this.uid).then(res => {
        if (res.success) {
          this.$message.success('退款成功')
          this.refundDialogVisible = false
          this.loadOrder()
        }
      }).finally(() => {
        this.refundSubmitting = false
      })
    },

  },

}
</script>

<style scoped>
@import "../../assets/css/ticket-item.css";

.refund-dialog__info {
  margin-top: 20px;
  padding: 16px 18px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  line-height: 2;
  color: #606266;
}

.refund-dialog__info span {
  display: inline-block;
  width: 48px;
  color: #909399;
}

.refund-dialog__price {
  font-style: normal;
  font-size: 18px;
  font-weight: 600;
  color: #e6a23c;
}
</style>
