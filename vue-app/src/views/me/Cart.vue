<template>
  <div class="ticket-page">
    <h3 class="ticket-page__title">购物车</h3>

    <el-alert
        v-if="cartList.length !== 0"
        class="ticket-page__tip"
        title="加入购物车后 10 分钟内座位为您保留；超时后锁座解除，他人可选。结算后请在 5 分钟内完成支付。"
        type="info"
        :closable="false"
        show-icon
    />

    <div v-loading="loading" class="ticket-list">
      <div v-for="(item, index) in cartList" :key="index" class="ticket-card">
        <div class="ticket-card__main">
          <div class="ticket-card__check">
            <el-checkbox @change="handleCheck" v-model="selectList[index].checked"/>
          </div>
          <div class="ticket-card__poster">
            <img class="ticket-card__cover" alt="" :src="item.film.cover"/>
          </div>

          <div class="ticket-card__info">
            <div>
              <p class="info-text info-text--title">《{{ item.film.name }}》</p>
              <p class="info-text">座位：{{ item.cart.seats }}</p>
              <p class="info-text">手机号码：{{ item.cart.phone }}</p>
              <p class="info-text info-text--accent">
                放映时间：{{ item.arrangement.date }} {{ item.arrangement.startTime }}
              </p>
              <p class="info-text info-text--price-row">
                <span class="info-price">￥{{ formatPrice(item.cart.price) }}</span>
              </p>
            </div>

            <div class="info-action-row">
              <el-button
                  class="info-action-btn"
                  type="danger"
                  plain
                  @click="handleDelete(index)">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && cartList.length === 0" class="ticket-empty">
        购物车是空的，去选座吧
      </div>
    </div>

    <div v-if="cartList.length !== 0" class="ticket-checkout">
      <div class="ticket-checkout__left">
        <el-checkbox @change="handleCheckAll" v-model="checkAll">全选</el-checkbox>
        <div class="ticket-checkout__total">
          合计<em>￥{{ formatPrice(price) }}</em>
        </div>
      </div>
      <el-button class="ticket-checkout__submit" type="danger" @click="submitCart">
        全部结算
      </el-button>
    </div>
  </div>
</template>

<script>
import {DeleteCartById, ListCarts} from "@/api/cart"
import {CreateOrder} from "@/api/order";

export default {
  data() {
    return {
      loading: false,
      price: 0,
      selectList: [],
      checkAll: false,
      cartList: [],
      uid: localStorage.getItem("uid"),
    }
  },

  mounted() {
    this.loadCarts()
  },

  methods: {

    formatPrice(price) {
      const n = Number(price)
      return Number.isNaN(n) ? price : n.toFixed(2).replace(/\.00$/, '')
    },

    loadCarts() {
      this.loading = true;
      ListCarts(this.uid).then(res => {
        setTimeout(() => {
          this.cartList = res.data
          this.selectList = []
          for (let i = 0; i < this.cartList.length; i++) {
            this.selectList[i] = {checked: false, cart: this.cartList[i].cart}
          }
          this.loading = false
        }, 700)
      })
    },

    handleCheck() {
      this.checkOut()
    },

    handleCheckAll() {
      if (this.checkAll) {
        this.changeAllChecked(true)
        this.checkOut()
      } else {
        this.changeAllChecked(false)
        this.price = 0
      }
    },

    changeAllChecked(status) {
      for (let i = 0; i < this.selectList.length; i++) {
        this.selectList[i].checked = status
      }
    },

    checkOut() {
      this.price = 0
      for (let i = 0; i < this.selectList.length; i++) {
        if (this.selectList[i].checked) {
          this.price += this.selectList[i].cart.price
        }
      }
    },

    handleDelete(index) {
      DeleteCartById(this.cartList[index].cart.id).then(res => {
        if (res.success) {
          this.cartList.splice(index, 1)
          this.selectList.splice(index, 1)
          this.checkOut()
        }
      })
    },

    submitCart() {
      const checkedItems = this.selectList.filter(item => item.checked)
      if (checkedItems.length === 0) {
        this.$message.warning('请先选择要结算的商品')
        return
      }

      this.$confirm(
          `请您仔细确认订单金额为 ${this.price} 元。订单生成后请在 5 分钟内完成支付，超时订单将失效且座位会被释放。`,
          '提示',
          {
            confirmButtonText: '确认支付',
            cancelButtonText: '取消支付',
            type: 'warning',
            center: true
          }
      ).then(async () => {
        this.loading = true
        const orderIds = []
        try {
          for (const item of checkedItems) {
            const res = await CreateOrder(item.cart)
            if (!res.success) {
              this.loadCarts()
              return
            }
            if (res.data && res.data.id) {
              orderIds.push(res.data.id)
              await DeleteCartById(item.cart.id)
            }
          }
          if (orderIds.length === 0) {
            this.$message.error('创建订单失败，请稍后重试')
            return
          }
          this.$message.warning('请在 5 分钟内完成支付')
          if (orderIds.length > 1) {
            this.$message.success('已生成 ' + orderIds.length + ' 个订单，其余订单可在「我的订单」中继续支付')
          }
          this.$router.push({ path: '/pay', query: { orderId: orderIds[0] } })
        } finally {
          this.loading = false
        }
      }).catch(() => {
        this.$message.warning('用户已取消支付')
      });
    },

  },

}
</script>

<style scoped>
@import "../../assets/css/ticket-item.css";
</style>
