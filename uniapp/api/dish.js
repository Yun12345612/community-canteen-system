import request from '../utils/request.js'

// 菜品接口管理
const dishApi = {
  /**
   * 【用户端】查询所有上架菜品
   * @returns {Promise} 上架菜品列表
   */
  getDishList() {
    return request({
      url: '/api/dish/list', 
      method: 'GET'
    })
  },

  /**
   * 【用户端】根据菜品ID查询详情
   * @param {Number} dishId 菜品ID
   * @returns {Promise} 菜品详情
   */
  getDishDetail(dishId) {
    return request({
      url: '/api/dish/detail', // 菜品详情接口路径
      method: 'GET',
      params: { dishId } // 传递菜品ID参数
    })
  },

  /**
   * 【商家端】查询当前商家所有菜品
   * @returns {Promise} 商家菜品列表
   */
  getMerchantDishList() {
    return request({
      url: '/merchant/dish/list',
      method: 'GET'
    })
  }
}

// 批量根据ID查询菜品（保留原商家端接口，备用）
export function getDishByIds(ids) {
  return request({
    url: '/merchant/dish/batch',
    method: 'get',
    params: { ids: ids.join(',') }
  })
}

// 导出核心接口方法（方便页面直接解构使用）
export const { getDishList, getDishDetail, getMerchantDishList } = dishApi
export default dishApi