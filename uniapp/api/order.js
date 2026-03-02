// api/order.js
import request from '../utils/request.js'

// 提交订单
export const submitOrder = (params) => {
  return request({
    url: "/user/order/submit",
    method: "POST",
    data: params
  })
}

// 查询用户订单列表
export const getOrderList = (userId) => {
  return request({
    url: '/user/order/list', 
    method: 'get',
    params: { userId }
  })
}

// 查询订单详情
export const getOrderDetail = (orderId) => {
  return request({
    url: '/user/order/detail', 
    method: 'get',
    params: { orderId }
  })
}
//模拟支付接口（假支付）
export const mockPay = (params) => {
  return request({
    url: '/user/order/mockPay', // 对应后端接口路径
    method: 'POST', // 后端是PostMapping，这里必须用POST
    data: params // 传订单ID，格式是 { orderId: 123 }
  })
}