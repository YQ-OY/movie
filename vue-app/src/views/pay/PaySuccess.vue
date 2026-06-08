<template>
  <div class="pay-success">
    <el-result
        icon="success"
        title="支付完成"
        :sub-title="subTitle"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/me/order')">查看我的订单</el-button>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const subTitle = computed(() => {
  const orderNo = route.query.out_trade_no
  const amount = route.query.total_amount
  if (orderNo && amount) {
    return `订单 ${orderNo} 已支付 ￥${amount}，状态将在通知回调后更新`
  }
  return '支付宝已受理您的付款，订单状态将在异步通知后更新'
})
</script>

<style scoped>
.pay-success {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
</style>
