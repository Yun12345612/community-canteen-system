"use strict";
const utils_request = require("../utils/request.js");
function wechatLogin(code) {
  return utils_request.request({
    url: "/user/wechatLogin",
    method: "POST",
    params: { code }
  });
}
function updateUserInfo(data) {
  return utils_request.request({
    url: "/user/updateUserInfo",
    method: "POST",
    data
  });
}
exports.updateUserInfo = updateUserInfo;
exports.wechatLogin = wechatLogin;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/user.js.map
