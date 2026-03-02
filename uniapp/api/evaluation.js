// /api/evaluation.js
import request from '../utils/request.js'

/**
 * 校验订单是否可评价
 * @param {string} orderId 订单UUID（必传）
 * @param {number} userId 用户ID（从登录态获取，也可放请求头）
 */
export function canEvaluate(orderId) {
  return request({
    url: `/user/evaluation/can-evaluate/${orderId}`,
    method: 'GET',

    params: {
      userId: uni.getStorageSync('userId') 
    }
  })
}

/**
 * 提交订单评价
 * @param {object} data 评价参数
 * @property {string} orderId 订单UUID
 * @property {string} [content] 整体评价文字（可选）
 * @property {array<string>} [imgUrls] 评价图片URL列表（可选）
 * @property {array<object>} dishEvaluations 菜品评价列表（必传）
 */
export function submitEvaluation(data) {
  return request({
    url: '/user/evaluation/submit',
    method: 'POST',
    data: {

      userId: uni.getStorageSync('userId'),
      ...data
    }
  })
}

/**
 * 查询用户的评价列表
 * @param {number} userId 用户ID
 */
export function getUserEvaluations() {
  return request({
    url: '/user/evaluation/list',
    method: 'GET',
    params: {
      userId: uni.getStorageSync('userId')
    }
  })
}


/**
 * 删除评价（删除后可以重新评价）
 * @param {string} orderId 订单ID
 */
export function deleteEvaluation(orderId) {
  return request({
    url: `/user/evaluation/delete/${orderId}`,
    method: 'POST'
  })
}

/**
 * 提交单独的菜品评价（不需要订单整体评价）
 * @param {object} data 评价参数
 * @property {string} orderId 订单UUID
 * @property {array<object>} dishEvaluations 菜品评价列表
 */
export function submitDishEvaluation(data) {
  return request({
    url: '/user/evaluation/submit-dish',
    method: 'POST',
    data: {
      userId: uni.getStorageSync('userId'),
      ...data
    }
  })
}

/**
 * 用户直接评价菜品（无需订单）
 * @param {object} data 评价参数
 * @property {number} dishId 菜品ID
 * @property {number} score 评分（1-5）
 * @property {string} content 评价内容（选填）
 */
export function submitDishReview(data) {
  return request({
    url: '/user/evaluation/dish-review',
    method: 'POST',
    data: {
      userId: uni.getStorageSync('userId'),
      ...data
    }
  })
}

/**
 * 获取菜品的评价列表
 * @param {number} dishId 菜品ID
 */
export function getDishReviews(dishId) {
  return request({
    url: `/user/evaluation/dish-reviews/${dishId}`,
    method: 'GET'
  })
}

/**
 * 获取订单的菜品评价列表（用于回显已评价数据）
 * @param {string} orderId 订单ID
 */
export function getDishEvaluationsByOrderId(orderId) {
  return request({
    url: `/user/evaluation/dish-evaluations/${orderId}`,
    method: 'GET'
  })
}
