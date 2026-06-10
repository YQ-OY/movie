import service from '@/utils/request'

export function confirmAlipayPayment(orderId) {
  return service({
    url: '/alipay/confirm',
    method: 'get',
    params: { orderId },
    silent: true,
  })
}
