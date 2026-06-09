<template>
  <div class="pay-jump">
    <el-alert
        title="请在 5 分钟内完成支付，超时后订单将失效且座位会被释放"
        type="warning"
        :closable="false"
        show-icon
        class="pay-alert"
    />
    <el-result
        icon="info"
        title="正在跳转支付宝沙箱"
        :sub-title="orderId ? `订单号：${orderId}` : '缺少订单号'"
    >
      <template #extra>
        <el-button type="primary" :disabled="!orderId" @click="goPay">立即支付</el-button>
        <el-button @click="$router.push('/me/order')">返回订单列表</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import config from '@/config'

const route = useRoute()
const orderId = computed(() => route.query.orderId)

const goPay = () => {
  if (!orderId.value) {
    return
  }
  window.open(`${config.API_URL}/alipay/pay?orderId=${orderId.value}`, '_self')
}

onMounted(() => {
  if (orderId.value) {
    goPay()
  }
})
</script>

<style scoped>
.pay-jump {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  padding: 24px;
}

.pay-alert {
  max-width: 480px;
  width: 100%;
  margin-bottom: 16px;
}
</style>
