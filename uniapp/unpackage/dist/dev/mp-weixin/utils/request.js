"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://127.0.0.1:8080";
const TIMEOUT = 1e4;
const request = (options) => {
  const WHITE_LIST = [
    "/user/wechatLogin",
    "/user/h5Login",
    "/merchant/dish/list",
    "/merchant/category/list/valid",
    "/api/recommend/user",
    // 新增：用户个性化推荐
    "/api/recommend/related",
    // 新增：菜品搭配推荐
    "/api/recommend/hybrid"
    // 新增：融合算法推荐
  ];
  const needAuth = !WHITE_LIST.some((url2) => options.url.includes(url2));
  const userToken = common_vendor.index.getStorageSync("token") || "";
  if (needAuth && !userToken) {
    common_vendor.index.showToast({
      title: "请先登录",
      icon: "none"
    });
    setTimeout(() => {
      common_vendor.index.reLaunch({ url: "/pages/login/login" });
    }, 1500);
    return Promise.reject(new Error("未登录，终止请求"));
  }
  let url = `${BASE_URL}${options.url}`;
  if (options.params && Object.keys(options.params).length > 0) {
    const paramsArr = [];
    Object.keys(options.params).forEach((key) => {
      paramsArr.push(`${key}=${encodeURIComponent(options.params[key])}`);
    });
    url += (url.includes("?") ? "&" : "?") + paramsArr.join("&");
  }
  const header = {
    "Content-Type": "application/json",
    ...options.header
  };
  if (userToken) {
    header.token = userToken;
  }
  return new Promise((resolve, reject) => {
    common_vendor.index.request({
      url,
      method: options.method || "GET",
      data: options.data || {},
      header,
      timeout: TIMEOUT,
      success: (res) => {
        const { statusCode, data } = res;
        if (statusCode === 401) {
          common_vendor.index.removeStorageSync("token");
          common_vendor.index.showToast({
            title: data.msg || "登录已过期，请重新登录",
            icon: "none"
          });
          setTimeout(() => {
            common_vendor.index.reLaunch({ url: "/pages/login/login" });
          }, 1500);
          reject(new Error("Token异常"));
          return;
        }
        if (statusCode !== 200) {
          common_vendor.index.showToast({
            title: `请求异常${statusCode}`,
            icon: "none"
          });
          reject(res);
          return;
        }
        if (data && typeof data === "object" && "code" in data) {
          if (data.code === 200) {
            resolve(data);
          } else {
            common_vendor.index.showToast({ title: data.msg || "请求失败", icon: "none" });
            reject(data);
          }
        } else {
          resolve(data);
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({
          title: "网络连接失败，请检查后端服务是否启动",
          icon: "none"
        });
        reject(err);
      }
    });
  });
};
exports.request = request;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/request.js.map
