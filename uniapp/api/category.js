import request from '../utils/request.js'

// 菜品分类接口管理
const categoryApi = {
  /**
   * 查询商家有效分类（小程序首页展示用）
   * @returns {Promise} 分类列表
   */
  getValidCategoryList() {
    return request({
      url: '/merchant/category/list/valid',
      method: 'GET'
    })
  },

  /**
   * 查询商家所有分类
   * @returns {Promise} 分类列表
   */
  getAllCategoryList() {
    return request({
      url: '/merchant/category/list/all',
      method: 'GET'
    })
  }
}

export default categoryApi