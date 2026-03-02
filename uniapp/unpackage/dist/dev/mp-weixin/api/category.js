"use strict";
const utils_request = require("../utils/request.js");
const categoryApi = {
  /**
   * 查询商家有效分类（小程序首页展示用）
   * @returns {Promise} 分类列表
   */
  getValidCategoryList() {
    return utils_request.request({
      url: "/merchant/category/list/valid",
      method: "GET"
    });
  },
  /**
   * 查询商家所有分类
   * @returns {Promise} 分类列表
   */
  getAllCategoryList() {
    return utils_request.request({
      url: "/merchant/category/list/all",
      method: "GET"
    });
  }
};
exports.categoryApi = categoryApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/category.js.map
