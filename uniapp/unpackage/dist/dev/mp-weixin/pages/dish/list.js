"use strict";
const common_vendor = require("../../common/vendor.js");
const api_category = require("../../api/category.js");
const api_dish = require("../../api/dish.js");
const utils_cartManager = require("../../utils/cartManager.js");
const customTabbar = () => "../../components/custom-tabbar/custom-tabbar.js";
const _sfc_main = {
  components: {
    customTabbar
  },
  data() {
    return {
      categoryList: [],
      allDishList: [],
      activeCategoryId: 0,
      showDishList: [],
      cartCount: 0,
      userId: common_vendor.index.getStorageSync("userId") || 1
    };
  },
  onShow() {
    this.loadCartCount();
  },
  onLoad() {
    this.getCategoryData();
    this.getDishData();
  },
  methods: {
    async getCategoryData() {
      try {
        const res = await api_category.categoryApi.getValidCategoryList();
        this.categoryList = (res == null ? void 0 : res.data) || [];
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/dish/list.vue:83", "获取分类失败：", err);
        common_vendor.index.showToast({ title: "分类加载失败", icon: "none" });
      }
    },
    async getDishData() {
      try {
        const res = await api_dish.dishApi.getDishList();
        this.allDishList = (res == null ? void 0 : res.data) || [];
        this.showDishList = this.allDishList;
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/dish/list.vue:93", "获取菜品失败：", err);
        common_vendor.index.showToast({ title: "菜品加载失败", icon: "none" });
      }
    },
    switchCategory(id) {
      this.activeCategoryId = id;
      this.showDishList = id === 0 ? this.allDishList : this.allDishList.filter((item) => (item == null ? void 0 : item.categoryId) === id);
    },
    async loadCartCount() {
      this.cartCount = await utils_cartManager.getCartTotalCount(this.userId);
    },
    async addCart(dish) {
      if (dish.status !== 1) {
        common_vendor.index.showToast({ title: "菜品已下架", icon: "none" });
        return;
      }
      try {
        await utils_cartManager.addCart(this.userId, dish.id, 1);
        await this.loadCartCount();
        common_vendor.index.showToast({ title: "加入购物车成功", icon: "success" });
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/dish/list.vue:114", "加入购物车失败：", err);
        common_vendor.index.showToast({ title: "库存不足", icon: "none" });
      }
    },
    goToCart() {
      common_vendor.index.navigateTo({ url: "/pages/cart/cart" });
    },
    goToDishDetail(dishId) {
      common_vendor.index.navigateTo({
        url: `/pages/dish/detail?dishId=${dishId}`
      });
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
    a: $data.activeCategoryId === 0 ? 1 : "",
    b: common_vendor.o(($event) => $options.switchCategory(0)),
    c: common_vendor.f($data.categoryList, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.categoryName),
        b: item.id,
        c: $data.activeCategoryId === item.id ? 1 : "",
        d: common_vendor.o(($event) => $options.switchCategory(item.id), item.id)
      };
    }),
    d: $data.showDishList.length === 0
  }, $data.showDishList.length === 0 ? {} : {
    e: common_vendor.f($data.showDishList, (dish, k0, i0) => {
      return common_vendor.e({
        a: dish && dish.coverImg || "/static/logo.png",
        b: common_vendor.o(($event) => dish.coverImg = "", dish.id),
        c: common_vendor.t(dish.dishName),
        d: common_vendor.t(dish && dish.description || "暂无描述"),
        e: common_vendor.t(dish.price.toFixed(2)),
        f: common_vendor.t(dish.status === 1 ? "上架中" : "已下架"),
        g: common_vendor.n(dish.status === 1 ? "status-on" : "status-off"),
        h: dish.status === 1
      }, dish.status === 1 ? {
        i: common_vendor.o(($event) => $options.addCart(dish), dish.id)
      } : {}, {
        j: dish.id,
        k: common_vendor.o(($event) => $options.goToDishDetail(dish.id), dish.id)
      });
    })
  }, {
    f: $data.cartCount > 0
  }, $data.cartCount > 0 ? {
    g: common_vendor.t($data.cartCount)
  } : {}, {
    h: common_vendor.o((...args) => $options.goToCart && $options.goToCart(...args)),
    i: common_vendor.p({
      ["current-index"]: 1
    })
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-d0592e28"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/dish/list.js.map
