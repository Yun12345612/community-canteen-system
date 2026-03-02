"use strict";
const common_vendor = require("../common/vendor.js");
const utils_request = require("../utils/request.js");
function canEvaluate(orderId) {
  return utils_request.request({
    url: `/user/evaluation/can-evaluate/${orderId}`,
    method: "GET",
    params: {
      userId: common_vendor.index.getStorageSync("userId")
    }
  });
}
function submitEvaluation(data) {
  return utils_request.request({
    url: "/user/evaluation/submit",
    method: "POST",
    data: {
      userId: common_vendor.index.getStorageSync("userId"),
      ...data
    }
  });
}
function getUserEvaluations() {
  return utils_request.request({
    url: "/user/evaluation/list",
    method: "GET",
    params: {
      userId: common_vendor.index.getStorageSync("userId")
    }
  });
}
function submitDishEvaluation(data) {
  return utils_request.request({
    url: "/user/evaluation/submit-dish",
    method: "POST",
    data: {
      userId: common_vendor.index.getStorageSync("userId"),
      ...data
    }
  });
}
function getDishEvaluationsByOrderId(orderId) {
  return utils_request.request({
    url: `/user/evaluation/dish-evaluations/${orderId}`,
    method: "GET"
  });
}
exports.canEvaluate = canEvaluate;
exports.getDishEvaluationsByOrderId = getDishEvaluationsByOrderId;
exports.getUserEvaluations = getUserEvaluations;
exports.submitDishEvaluation = submitDishEvaluation;
exports.submitEvaluation = submitEvaluation;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/evaluation.js.map
