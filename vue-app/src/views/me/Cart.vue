<template>
  <div style="width: 100%">
    <h3 style="letter-spacing: 1px;font-weight: 400;padding-bottom: 20px">购物车</h3>

    <div v-loading="loading">

      <el-card v-for="(item, index) in cartList" :key="index" class="box-card">
        <div>
          <div style="float: left;line-height: 100px;padding-right: 20px">
            <el-checkbox @change="handleCheck" v-model="selectList[index].checked"></el-checkbox>
          </div>
          <img class="item-film-img" alt="" src="../../assets/img/film.png"/>
          <div style="float: left;">
            <div class="item-film-name">《{{ item.film.name }}》</div>
            <div class="item-film-seat">座位 : {{ item.cart.seats }}</div>
            <div class="item-film-seat">手机号码 : {{ item.cart.phone }}</div>
            <div class="item-film-time">开场时间 :
              <span style="color: red">{{ item.arrangement.date }} - {{ item.arrangement.startTime }}</span>
            </div>
          </div>
          <div style="float: right;color: #f34d41;letter-spacing: 2px; line-height: 100px;padding-right: 5px">
            ￥{{ item.cart.price }}
            <el-button @click="handleDelete(index)"
                       link
                       style="color: red;padding-left: 50px"
                       :icon="Delete">删除
            </el-button>
          </div>
        </div>
      </el-card>

      <div v-if="cartList.length !==0" style="height: 60px;background: #91949c">
        <div>
          <div style="float: left;line-height: 60px;padding: 0 20px;font-size: 14px">
            <el-checkbox @change="handleCheckAll" v-model="checkAll">全 选</el-checkbox>
          </div>
          <el-button @click="submitCart" link class="sub-btn">全部结算</el-button>
          <div class="order-footer1">合计 : ￥{{ price }}</div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import {DeleteCartById, ListCarts} from "@/api/cart"
import {CreateOrder} from "@/api/order";
import { Delete } from '@element-plus/icons-vue'

export default {
  components: { Delete },
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

      this.$confirm('请您仔细确认订单金额为' + this.price + '元, 确认后将跳转至支付宝沙箱完成支付', '提示', {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消支付',
        type: 'success',
        center: true
      }).then(async () => {
        this.loading = true
        const orderIds = []
        try {
          for (const item of checkedItems) {
            item.cart.status = 0
            const res = await CreateOrder(item.cart)
            if (res.success && res.data && res.data.id) {
              orderIds.push(res.data.id)
              await DeleteCartById(item.cart.id)
            }
          }
          if (orderIds.length === 0) {
            this.$message.error('创建订单失败，请稍后重试')
            return
          }
          if (orderIds.length > 1) {
            this.$message.success('已生成 ' + orderIds.length + ' 个订单，其余订单可在「我的订单」中继续支付')
          }
          this.$router.push({ path: '/pay', query: { orderId: orderIds[0] } })
        } finally {
          this.loading = false
        }
      }).catch(() => {
        this.$message({
          type: 'warning',
          message: '用户已取消支付'
        });
      });
    },

  },

}
</script>

<style scoped>

.box-card {
  margin-bottom: 5px;
}

:deep(.el-card__header) {
  background: #C0C4CC;
}

.item-film-img {
  float: left;
  width: 80px;
  height: 100px;
  padding-bottom: 20px;
  padding-right: 10px;
}

.item-film-name {
  letter-spacing: 2px;
  font-weight: 500;
  font-size: 18px;
  padding-bottom: 13px;
}

.item-film-seat {
  letter-spacing: 1px;
  font-size: 12px;
  padding-bottom: 8px;
  padding-left: 10px;
  color: #91949c;
}


.item-film-time {
  font-size: 12px;
  padding-left: 10px;
  letter-spacing: 1px;
  color: #91949c;
}

.sub-btn {
  float: right;
  color: #000000;
  height: 60px;
  width: 120px;
  background: #f34d41;
  letter-spacing: 2px;
  line-height: 33px;
  text-align: center;
  font-weight: 500;
  font-size: 18px;
}

.order-footer1 {
  float: right;
  line-height: 60px;
  padding-right: 40px;
}

a {
  color: #333333;
}

.di {
  text-align: center;
  font-size: 20px;
  letter-spacing: 3px;
  font-weight: bold;
  padding-top: 10px;
}
</style>
