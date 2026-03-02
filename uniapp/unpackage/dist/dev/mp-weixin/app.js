"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/login/login.js";
  "./pages/index/index.js";
  "./pages/dish/list.js";
  "./pages/dish/detail.js";
  "./pages/order/list.js";
  "./pages/mine/mine.js";
  "./pages/cart/cart.js";
  "./pages/order/confirm.js";
  "./pages/order/pay-result.js";
  "./pages/order/evaluation.js";
  "./pages/order/dish-evaluation.js";
  "./pages/evaluation/list.js";
  "./pages/evaluation/debug.js";
}
const _sfc_main = {
  globalData: {
    // 仅保留必要全局数据，购物车逻辑移至工具类
    merchantId: 1
  },
  onLaunch() {
    common_vendor.index.__f__("log", "at App.vue:8", "=== App 实例已初始化 ===");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
