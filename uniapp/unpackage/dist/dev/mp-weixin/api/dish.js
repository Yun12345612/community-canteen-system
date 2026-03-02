"use strict";
const utils_request = require("../utils/request.js");
const dishApi = {
  /**
   * 【用户端】查询所有上架菜品
   * @returns {Promise} 上架菜品列表
   */
  getDishList() {
    return utils_request.request({
      url: "/api/dish/list",
      method: "GET"
    });
  },
  /**
   * 【用户端】根据菜品ID查询详情
   * @param {Number} dishId 菜品ID
   * @returns {Promise} 菜品详情
   */
  getDishDetail(dishId) {
    return utils_request.request({
      url: "/api/dish/detail",
      // 菜品详情接口路径
      method: "GET",
      params: { dishId }
      // 传递菜品ID参数
    });
  },
  /**
   * 【商家端】查询当前商家所有菜品
   * @returns {Promise} 商家菜品列表
   */
  getMerchantDishList() {
    return utils_request.request({
      url: "/merchant/dish/list",
      method: "GET"
    });
  }
};
const { getDishList, getDishDetail, getMerchantDishList } = dishApi;
exports.dishApi = dishApi;
exports.getDishDetail = getDishDetail;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/dish.js.map
