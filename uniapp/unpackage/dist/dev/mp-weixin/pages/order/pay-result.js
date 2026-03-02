"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      status: "success"
    };
  },
  onLoad(options) {
    this.status = options.status || "fail";
  },
  methods: {
    backHome() {
      common_vendor.index.redirectTo({ url: "/pages/dish/list" });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.status === "success"
  }, $data.status === "success" ? {} : {}, {
    b: common_vendor.t($data.status === "success" ? "支付成功" : "支付失败"),
    c: common_vendor.t($data.status === "success" ? "商家已收到订单，开始准备餐品" : "支付未完成，可返回重新下单"),
    d: common_vendor.o((...args) => $options.backHome && $options.backHome(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-045a41fe"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/pay-result.js.map
