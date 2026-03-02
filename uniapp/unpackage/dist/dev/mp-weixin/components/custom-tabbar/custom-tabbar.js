"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  name: "custom-tabbar",
  props: {
    currentIndex: {
      type: Number,
      default: 0
    }
  },
  methods: {
    jump(index) {
      const paths = [
        "/pages/index/index",
        "/pages/dish/list",
        "/pages/order/list",
        "/pages/mine/mine"
      ];
      common_vendor.index.reLaunch({ url: paths[index] });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $props.currentIndex === 0 ? 1 : "",
    b: common_vendor.o(($event) => $options.jump(0)),
    c: $props.currentIndex === 1 ? 1 : "",
    d: common_vendor.o(($event) => $options.jump(1)),
    e: $props.currentIndex === 2 ? 1 : "",
    f: common_vendor.o(($event) => $options.jump(2)),
    g: $props.currentIndex === 3 ? 1 : "",
    h: common_vendor.o(($event) => $options.jump(3))
  };
}
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-51c48e3c"]]);
wx.createComponent(Component);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/components/custom-tabbar/custom-tabbar.js.map
