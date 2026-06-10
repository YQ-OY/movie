import service from "../utils/request";

export function FindAllOrder() {
    return service({
        url: '/order',
        method: 'get'
    })
}

// 分页查询订单
export function listOrderPage(params) {
  return service({
    url: '/order/page',
    method: 'get',
    params
  })
}

export function UpdateOrder(data) {
    return service({
        url: '/order',
        method: 'put',
        data: data
    })
}

export function AdminRefundOrder(id) {
    return service({
        url: '/order/refund/admin?id=' + id,
        method: 'post'
    })
}