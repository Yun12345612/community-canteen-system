"use strict";
const utils_request = require("../utils/request.js");
const submitOrder = (params) => {
  return utils_request.request({
    url: "/user/order/submit",
    method: "POST",
    data: params
  });
};
const getOrderList = (userId) => {
  return utils_request.request({
    url: "/user/order/list",
    method: "get",
    params: { userId }
  });
};
const getOrderDetail = (orderId) => {
  return utils_request.request({
    url: "/user/order/detail",
    method: "get",
    params: { orderId }
  });
};
const mockPay = (params) => {
  return utils_request.request({
    url: "/user/order/mockPay",
    // 对应后端接口路径
    method: "POST",
    // 后端是PostMapping，这里必须用POST
    data: params
    // 传订单ID，格式是 { orderId: 123 }
  });
};
exports.getOrderDetail = getOrderDetail;
exports.getOrderList = getOrderList;
exports.mockPay = mockPay;
exports.submitOrder = submitOrder;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/order.js.map
