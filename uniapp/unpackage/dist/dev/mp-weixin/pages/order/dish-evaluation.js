"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const api_evaluation = require("../../api/evaluation.js");
const _sfc_main = {
  data() {
    return {
      orderId: "",
      orderNo: "",
      dishList: [],
      submitting: false,
      hasEvaluated: false
    };
  },
  onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId;
      this.loadOrderDishes();
    } else {
      common_vendor.index.showToast({
        title: "订单ID不能为空",
        icon: "none"
      });
      setTimeout(() => {
        common_vendor.index.navigateBack();
      }, 1500);
    }
  },
  methods: {
    // 加载订单菜品
    async loadOrderDishes() {
      try {
        common_vendor.index.showLoading({ title: "加载中..." });
        const [orderRes, evalRes] = await Promise.all([
          api_order.getOrderDetail(this.orderId),
          api_evaluation.getDishEvaluationsByOrderId(this.orderId)
        ]);
        common_vendor.index.__f__("log", "at pages/order/dish-evaluation.vue:111", "订单详情返回数据:", JSON.stringify(orderRes));
        common_vendor.index.__f__("log", "at pages/order/dish-evaluation.vue:112", "菜品评价返回数据:", JSON.stringify(evalRes));
        if (orderRes.code === 200 && orderRes.data) {
          this.orderNo = orderRes.data.orderNo;
          const items = orderRes.data.dishItems || orderRes.data.items || [];
          this.dishList = items.map((item) => ({
            orderItemId: item.id || item.orderItemId,
            dishId: item.dishId,
            dishName: item.dishName,
            dishImage: item.dishImage,
            price: item.unitPrice || item.price,
            quantity: item.count || item.quantity,
            score: 5,
            content: ""
          }));
          if (evalRes.code === 200 && evalRes.data && evalRes.data.length > 0) {
            this.hasEvaluated = true;
            common_vendor.index.__f__("log", "at pages/order/dish-evaluation.vue:133", "开始匹配菜品评价数据");
            evalRes.data.forEach((evalItem) => {
              common_vendor.index.__f__("log", "at pages/order/dish-evaluation.vue:136", "评价数据项:", JSON.stringify(evalItem));
              const evalOrderItemId = String(evalItem.orderItemId);
              const dish = this.dishList.find((d) => String(d.orderItemId) === evalOrderItemId);
              common_vendor.index.__f__("log", "at pages/order/dish-evaluation.vue:140", "匹配结果 - evalOrderItemId:", evalOrderItemId, "找到:", !!dish);
              if (dish) {
                dish.score = evalItem.score;
                dish.content = evalItem.content || "";
                common_vendor.index.__f__("log", "at pages/order/dish-evaluation.vue:144", "回显成功 - dishName:", dish.dishName, "score:", dish.score);
              }
            });
          }
          if (this.dishList.length === 0) {
            common_vendor.index.showToast({
              title: "该订单暂无菜品信息",
              icon: "none"
            });
          }
        } else {
          throw new Error(orderRes.msg || "加载订单失败");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/dish-evaluation.vue:159", "加载订单菜品失败:", error);
        common_vendor.index.showToast({
          title: error.message || "加载失败",
          icon: "none"
        });
      } finally {
        common_vendor.index.hideLoading();
      }
    },
    // 设置评分
    setRating(index, score) {
      if (!this.hasEvaluated) {
        this.dishList[index].score = score;
      }
    },
    // 提交评价
    async submitEvaluation() {
      if (this.hasEvaluated) {
        common_vendor.index.showToast({
          title: "您已评价过",
          icon: "none"
        });
        return;
      }
      if (!this.dishList || this.dishList.length === 0) {
        common_vendor.index.showToast({
          title: "暂无菜品信息，请稍后重试",
          icon: "none"
        });
        return;
      }
      const invalidDish = this.dishList.find((dish) => !dish.orderItemId || !dish.dishId);
      if (invalidDish) {
        common_vendor.index.__f__("error", "at pages/order/dish-evaluation.vue:198", "菜品数据不完整:", invalidDish);
        common_vendor.index.showToast({
          title: "菜品数据异常，请重新进入",
          icon: "none"
        });
        return;
      }
      this.submitting = true;
      try {
        const evaluationData = {
          orderId: this.orderId,
          dishEvaluations: this.dishList.map((dish) => ({
            orderItemId: dish.orderItemId,
            dishId: dish.dishId,
            score: dish.score,
            content: dish.content || ""
          }))
        };
        common_vendor.index.__f__("log", "at pages/order/dish-evaluation.vue:219", "提交菜品评价数据:", JSON.stringify(evaluationData));
        const res = await api_evaluation.submitDishEvaluation(evaluationData);
        if (res.code === 200) {
          this.hasEvaluated = true;
          common_vendor.index.showToast({
            title: "评价成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        } else {
          throw new Error(res.msg || "提交失败");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/dish-evaluation.vue:235", "提交评价失败:", error);
        common_vendor.index.showToast({
          title: error.message || "提交失败",
          icon: "none"
        });
      } finally {
        this.submitting = false;
      }
    },
    // 返回
    goBack() {
      common_vendor.index.navigateBack();
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    b: common_vendor.t($data.orderNo),
    c: $data.hasEvaluated
  }, $data.hasEvaluated ? {} : {}, {
    d: common_vendor.f($data.dishList, (dish, index, i0) => {
      var _a;
      return {
        a: dish.dishImage || "/static/default-dish.png",
        b: common_vendor.t(dish.dishName),
        c: common_vendor.t(dish.price),
        d: common_vendor.t(dish.quantity),
        e: common_vendor.f(5, (star, k1, i1) => {
          return {
            a: common_vendor.t(star <= dish.score ? "★" : "☆"),
            b: star,
            c: common_vendor.o(($event) => $options.setRating(index, star), star)
          };
        }),
        f: dish.content,
        g: common_vendor.o(($event) => dish.content = $event.detail.value, index),
        h: common_vendor.t(((_a = dish.content) == null ? void 0 : _a.length) || 0),
        i: index
      };
    }),
    e: $data.hasEvaluated,
    f: common_vendor.t($data.hasEvaluated ? "已评价" : $data.submitting ? "提交中..." : "提交评价"),
    g: common_vendor.o((...args) => $options.submitEvaluation && $options.submitEvaluation(...args)),
    h: $data.submitting || $data.hasEvaluated
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-b0114639"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/dish-evaluation.js.map
