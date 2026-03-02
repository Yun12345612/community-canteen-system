"use strict";
const utils_request = require("../utils/request.js");
function getUserRecommend(userId, topN) {
  return utils_request.request({
    url: "/api/recommend/user",
    method: "get",
    params: { userId, topN }
  });
}
function getRelatedRecommend(dishId, topN) {
  return utils_request.request({
    url: "/api/recommend/related",
    method: "get",
    params: { dishId, topN }
  });
}
function getHybridRecommend(userId, topN) {
  return utils_request.request({
    url: "/api/recommend/hybrid",
    method: "get",
    params: { userId, topN }
  });
}
exports.getHybridRecommend = getHybridRecommend;
exports.getRelatedRecommend = getRelatedRecommend;
exports.getUserRecommend = getUserRecommend;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/recommend.js.map
