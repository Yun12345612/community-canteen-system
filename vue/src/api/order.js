
// src/api/order.js
import request from '../utils/request';

/**
 * 商家订单相关API
 * 适配后端：TMerchantOrderController
 */

// 1. 分类查询商家订单（未完成/已完成）
export function getMerchantOrderList(params) {
  return request({
    url: '/merchant/order/list',
    method: 'get',
    params: params // 传URL参数：{isCompleted}
  });
}

// 2. 商家完成订单操作
export function completeMerchantOrder(params) {
  return request({
    url: '/merchant/order/complete',
    method: 'post',
    params: params // 传URL参数：{orderId}
  });
}

// 3. 商家取消订单操作
export function cancelMerchantOrder(params) {
  return request({
    url: '/merchant/order/cancel',
    method: 'post',
    params: params // 传URL参数：{orderId, cancelReason}
  });
}

//4.商家恢复订单
export function recoverMerchantOrder(data) {
  return request({
    url: '/merchant/order/recover',
    method: 'post',
    params: data // data包含orderId
  })
}