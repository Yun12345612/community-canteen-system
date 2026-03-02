"use strict";
const common_vendor = require("../../common/vendor.js");
const api_dish = require("../../api/dish.js");
const api_recommend = require("../../api/recommend.js");
const utils_cartManager = require("../../utils/cartManager.js");
const _sfc_main = {
  data() {
    return {
      dishId: "",
      dish: null,
      relatedList: [],
      userId: common_vendor.index.getStorageSync("userId") || 1,
      loading: true,
      jumping: false
    };
  },
  computed: {
    filteredRelatedList() {
      return this.relatedList.filter((item) => {
        if (!item)
          return false;
        return item.id != null && item.id !== "";
      });
    }
  },
  onLoad(options) {
    if (!(options == null ? void 0 : options.dishId) || isNaN(Number(options.dishId))) {
      common_vendor.index.showToast({ title: "菜品ID不合法", icon: "none" });
      setTimeout(() => common_vendor.index.navigateBack(), 1e3);
      return;
    }
    this.dishId = Number(options.dishId);
    this.getDishDetail();
    this.getRelatedRecommend();
  },
  methods: {
    async getDishDetail() {
      try {
        this.loading = true;
        const res = await api_dish.getDishDetail(this.dishId);
        this.dish = (res == null ? void 0 : res.data) || res || {};
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/dish/detail.vue:113", "获取菜品详情失败：", err);
        this.dish = null;
        common_vendor.index.showToast({ title: "菜品详情加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    async getRelatedRecommend() {
      try {
        if (!this.dishId)
          return;
        const res = await api_recommend.getRelatedRecommend(this.dishId, 3);
        const rawList = (res == null ? void 0 : res.data) || res || [];
        this.relatedList = Array.isArray(rawList) ? rawList : [rawList];
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/dish/detail.vue:127", "获取搭配推荐失败：", err);
        this.relatedList = [];
      }
    },
    async addCart() {
      var _a;
      if (!((_a = this.dish) == null ? void 0 : _a.id)) {
        common_vendor.index.showToast({ title: "菜品信息异常", icon: "none" });
        return;
      }
      if (this.dish.status !== 1) {
        common_vendor.index.showToast({ title: "菜品已下架", icon: "none" });
        return;
      }
      if ((this.dish.stock || 0) <= 0) {
        common_vendor.index.showToast({ title: "库存不足", icon: "none" });
        return;
      }
      try {
        await utils_cartManager.addCart(this.userId, this.dish.id, 1);
        common_vendor.index.showToast({ title: "加入购物车成功", icon: "success" });
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/dish/detail.vue:148", "加入购物车失败：", err);
        common_vendor.index.showToast({ title: "加入购物车失败", icon: "none" });
      }
    },
    goToDishDetail(dishId) {
      if (!dishId || this.jumping)
        return;
      this.jumping = true;
      common_vendor.index.redirectTo({
        url: `/pages/dish/detail?dishId=${dishId}`,
        fail: () => {
          this.jumping = false;
          common_vendor.index.showToast({ title: "跳转失败", icon: "none" });
        },
        complete: () => setTimeout(() => this.jumping = false, 500)
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.dish && $data.dish.id
  }, $data.dish && $data.dish.id ? common_vendor.e({
    b: $data.dish.coverImg || "/static/logo.png",
    c: common_vendor.o(($event) => $data.dish.coverImg = ""),
    d: common_vendor.t($data.dish.dishName || "未知菜品"),
    e: common_vendor.t(($data.dish.price || 0).toFixed(2)),
    f: common_vendor.t($data.dish.description || "暂无菜品描述"),
    g: $data.dish.status === 1 && ($data.dish.stock || 0) > 0
  }, $data.dish.status === 1 && ($data.dish.stock || 0) > 0 ? {
    h: common_vendor.o((...args) => $options.addCart && $options.addCart(...args))
  } : {}, {
    i: $data.dish.status !== 1
  }, $data.dish.status !== 1 ? {} : ($data.dish.stock || 0) <= 0 ? {} : {}, {
    j: ($data.dish.stock || 0) <= 0,
    k: $options.filteredRelatedList.length > 0
  }, $options.filteredRelatedList.length > 0 ? {
    l: common_vendor.f($options.filteredRelatedList, (item, index, i0) => {
      return {
        a: item.coverImg || "/static/logo.png",
        b: common_vendor.o(($event) => item.coverImg = "", item.id || index),
        c: common_vendor.t(item.dishName || "未知菜品"),
        d: common_vendor.t((item.price || 0).toFixed(2)),
        e: item.id || index,
        f: common_vendor.o(($event) => $options.goToDishDetail(item.id), item.id || index)
      };
    })
  } : {}, {
    m: !$data.loading && $options.filteredRelatedList.length === 0
  }, !$data.loading && $options.filteredRelatedList.length === 0 ? {} : {}) : {
    n: common_vendor.t($data.loading ? "菜品详情加载中..." : "暂无菜品信息")
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-de90b20a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/dish/detail.js.map
