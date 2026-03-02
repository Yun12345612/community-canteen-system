"use strict";
const common_vendor = require("../../common/vendor.js");
const api_recommend = require("../../api/recommend.js");
const customTabbar = () => "../../components/custom-tabbar/custom-tabbar.js";
const _sfc_main = {
  // 注册组件
  components: {
    customTabbar
  },
  data() {
    return {
      recommendList: [],
      userId: common_vendor.index.getStorageSync("userId") || 1,
      loading: true
    };
  },
  computed: {
    filteredRecommendList() {
      return this.recommendList.filter((dish) => {
        if (!dish)
          return false;
        return typeof dish.id === "number" && dish.id > 0;
      });
    }
  },
  onLoad() {
    this.fetchHybridRecommend();
  },
  methods: {
    goToDishList() {
      common_vendor.index.redirectTo({ url: "/pages/dish/list" });
    },
    goToOrderList() {
      common_vendor.index.redirectTo({ url: "/pages/order/list" });
    },
    goToDishDetail(dishId) {
      if (!dishId)
        return;
      common_vendor.index.navigateTo({ url: `/pages/dish/detail?dishId=${dishId}` });
    },
    async fetchUserRecommend() {
      try {
        this.loading = true;
        const res = await api_recommend.getUserRecommend(this.userId, 5);
        const rawList = (res == null ? void 0 : res.data) || res || [];
        this.recommendList = Array.isArray(rawList) ? rawList : [];
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:115", "获取猜你喜欢失败：", err);
        this.recommendList = [];
      } finally {
        this.loading = false;
      }
    },
    async fetchHybridRecommend() {
      try {
        this.loading = true;
        const res = await api_recommend.getHybridRecommend(this.userId, 5);
        const rawList = (res == null ? void 0 : res.data) || res || [];
        this.recommendList = Array.isArray(rawList) ? rawList : [];
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:128", "获取融合推荐失败：", err);
        this.recommendList = [];
      } finally {
        this.loading = false;
      }
    }
  }
};
if (!Array) {
  const _easycom_custom_tabbar2 = common_vendor.resolveComponent("custom-tabbar");
  _easycom_custom_tabbar2();
}
const _easycom_custom_tabbar = () => "../../components/custom-tabbar/custom-tabbar.js";
if (!Math) {
  _easycom_custom_tabbar();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o((...args) => $options.goToDishList && $options.goToDishList(...args)),
    b: common_vendor.o((...args) => $options.goToOrderList && $options.goToOrderList(...args)),
    c: $options.filteredRecommendList.length > 0
  }, $options.filteredRecommendList.length > 0 ? {
    d: common_vendor.o((...args) => $options.goToDishList && $options.goToDishList(...args)),
    e: common_vendor.f($options.filteredRecommendList, (dish, index, i0) => {
      return {
        a: dish.coverImg || "/static/logo.png",
        b: common_vendor.o(($event) => dish.coverImg = "", dish.id || index),
        c: common_vendor.t(dish.dishName || "未知菜品"),
        d: common_vendor.t((dish.price || 0).toFixed(2)),
        e: dish.id || index,
        f: common_vendor.o(($event) => $options.goToDishDetail(dish.id), dish.id || index)
      };
    })
  } : {}, {
    f: !$data.loading && $options.filteredRecommendList.length === 0
  }, !$data.loading && $options.filteredRecommendList.length === 0 ? {} : {}, {
    g: common_vendor.p({
      ["current-index"]: 0
    })
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
