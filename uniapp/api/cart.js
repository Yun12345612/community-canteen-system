// api/cart.js
import request from '../utils/request.js'

// 加入购物车
export const addCartApi = (data) => {
  return request({
    url: '/cart/add',
    method: 'POST',
    data: data 
  })
}

// 查询购物车列表
export const getCartListApi = (data) => {
  return request({
    url: '/cart/list',
    method: 'POST',
    data: data 
  })
}

// 更新购物车数量
export const updateCartCountApi = (data) => {
  return request({
    url: '/cart/update',
    method: 'PUT',
    data: data
  })
}

// 清空购物车
export const clearCartApi = (data) => {
  return request({
    url: '/cart/clear',
    method: 'DELETE',
    data: data
  })
}