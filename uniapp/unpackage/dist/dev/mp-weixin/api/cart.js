"use strict";
const utils_request = require("../utils/request.js");
const addCartApi = (data) => {
  return utils_request.request({
    url: "/cart/add",
    method: "POST",
    data
  });
};
const getCartListApi = (data) => {
  return utils_request.request({
    url: "/cart/list",
    method: "POST",
    data
  });
};
const updateCartCountApi = (data) => {
  return utils_request.request({
    url: "/cart/update",
    method: "PUT",
    data
  });
};
exports.addCartApi = addCartApi;
exports.getCartListApi = getCartListApi;
exports.updateCartCountApi = updateCartCountApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/cart.js.map
