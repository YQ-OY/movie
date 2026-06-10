<template>
  <div class="pay-success">
    <el-result
        :icon="resultIcon"
        :title="resultTitle"
        :sub-title="resultSubTitle"
    >
      <template #extra>
        <el-button type="primary" @click="goOrders">查看我的订单</el-button>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { confirmAlipayPayment } from '@/api/alipay'
import { openProfileCenter } from '@/utils/profileCenter'

const route = useRoute()
const router = useRouter()
const syncing = ref(false)
const syncDone = ref(false)
const syncFailed = ref(false)
const orderStatus = ref(null)

const orderNo = computed(() => route.query.out_trade_no)
const amount = computed(() => route.query.total_amount)
const returnError = computed(() => route.query.error)

const resultIcon = computed(() => {
  if (syncing.value) return 'info'
  if (orderStatus.value === 2) return 'success'
  if (syncFailed.value) return 'warning'
  return 'info'
})

const resultTitle = computed(() => {
  if (syncing.value) return '正在确认支付结果'
  if (orderStatus.value === 2) return '支付成功'
  if (syncFailed.value) return '支付结果待确认'
  return '支付完成'
})

const resultSubTitle = computed(() => {
  if (syncing.value) {
    return orderNo.value ? `订单 ${orderNo.value} 支付结果同步中…` : '正在向服务器确认支付状态…'
  }
  if (orderStatus.value === 2) {
    const priceText = amount.value ? `，金额 ￥${amount.value}` : ''
    return `订单 ${orderNo.value} 已支付成功${priceText}`
  }
  if (returnError.value === 'sign') {
    return '支付回跳验签异常，请稍后在「我的订单」中查看状态或联系客服'
  }
  if (syncFailed.value) {
    return '支付宝已受理付款，但本地订单状态尚未更新。请稍后刷新「我的订单」，或重新进入该页面重试'
  }
  if (orderNo.value && amount.value) {
    return `订单 ${orderNo.value} 已支付 ￥${amount.value}`
  }
  return '支付宝已受理您的付款'
})

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms))

const goOrders = () => {
  openProfileCenter(router, 'order')
}

async function syncPaymentStatus() {
  if (!orderNo.value) {
    syncDone.value = true
    return
  }
  syncing.value = true
  const maxAttempts = 4
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try {
      const res = await confirmAlipayPayment(orderNo.value)
      if (res?.success && res.data?.status === 2) {
        orderStatus.value = 2
        syncFailed.value = false
        syncing.value = false
        syncDone.value = true
        return
      }
    } catch (e) {
      // 支付宝侧可能尚未落库，稍后重试
    }
    if (attempt < maxAttempts) {
      await sleep(1500)
    }
  }
  syncing.value = false
  syncDone.value = true
  syncFailed.value = orderStatus.value !== 2
}

onMounted(() => {
  syncPaymentStatus()
})
</script>

<style scoped>
.pay-success {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--app-gradient-page);
}
</style>
