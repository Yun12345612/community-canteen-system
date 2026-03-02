"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_cartManager = require("../../utils/cartManager.js");
const _sfc_main = {
  data() {
    return {
      cartList: [],
      totalPrice: "0.00",
      userId: common_vendor.index.getStorageSync("userId") || 1
    };
  },
  onShow() {
    this.initData();
  },
  methods: {
    async initData() {
      try {
        this.cartList = await utils_cartManager.getCartList(this.userId);
        this.calcTotalPrice();
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/cart/cart.vue:50", "获取购物车失败：", err);
        common_vendor.index.showToast({ title: "购物车加载失败", icon: "none" });
      }
    },
    async updateCount(dishId, count) {
      if (count < 1) {
        common_vendor.index.showToast({ title: "数量不能小于1", icon: "none" });
        return;
      }
      try {
        await utils_cartManager.updateCartCount(this.userId, dishId, count);
        this.initData();
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/cart/cart.vue:65", "更新数量失败：", err);
        common_vendor.index.showToast({ title: "操作失败", icon: "none" });
      }
    },
    toConfirm() {
      if (this.cartList.length === 0) {
        common_vendor.index.showToast({ title: "请选择商品", icon: "none" });
        return;
      }
      common_vendor.index.navigateTo({ url: "/pages/order/confirm" });
    },
    goToDish() {
      common_vendor.index.reLaunch({ url: "/pages/dish/list" });
    },
    calcTotalPrice() {
      const total = this.cartList.reduce((sum, item) => {
        return sum + (item.price || 0) * item.count;
      }, 0);
      this.totalPrice = total.toFixed(2);
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.cartList.length === 0
  }, $data.cartList.length === 0 ? {
    b: common_vendor.o((...args) => $options.goToDish && $options.goToDish(...args))
  } : {
    c: common_vendor.f($data.cartList, (item, k0, i0) => {
      return {
        a: item.coverImg || "/static/logo.png",
        b: common_vendor.o(($event) => item.coverImg = "", item.id),
        c: common_vendor.t(item.dishName),
        d: common_vendor.t((item.price || 0).toFixed(2)),
        e: common_vendor.o(($event) => $options.updateCount(item.dishId, item.count - 1), item.id),
        f: common_vendor.t(item.count),
        g: common_vendor.o(($event) => $options.updateCount(item.dishId, item.count + 1), item.id),
        h: item.id
      };
    }),
    d: common_vendor.t($data.totalPrice),
    e: common_vendor.o((...args) => $options.toConfirm && $options.toConfirm(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-c91e7611"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/cart/cart.js.map
