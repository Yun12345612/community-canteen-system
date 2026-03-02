"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const api_evaluation = require("../../api/evaluation.js");
const customTabbar = () => "../../components/custom-tabbar/custom-tabbar.js";
const _sfc_main = {
  // 注册导航栏组件
  components: {
    customTabbar
  },
  data() {
    return {
      orderList: [],
      loading: false,
      userId: common_vendor.index.getStorageSync("userId") || 1
      // 从缓存取用户ID
    };
  },
  onShow() {
    this.getOrderData();
  },
  methods: {
    // 获取订单列表
    async getOrderData() {
      this.loading = true;
      try {
        const res = await api_order.getOrderList(this.userId);
        this.orderList = res.data || [];
        for (let item of this.orderList) {
          if (item.orderStatus === 3) {
            item.isEvaluating = true;
            try {
              const evalRes = await api_evaluation.canEvaluate(String(item.id));
              item.isEvaluated = !evalRes.data;
            } catch (err) {
              common_vendor.index.__f__("error", "at pages/order/list.vue:143", `校验订单${item.id}可评价状态失败：`, err);
              item.isEvaluated = true;
            } finally {
              item.isEvaluating = false;
            }
          } else {
            item.isEvaluated = true;
          }
        }
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/order/list.vue:153", "获取订单列表失败：", err);
        common_vendor.index.showToast({ title: "获取订单失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    // 跳转到点餐页面
    goToDish() {
      common_vendor.index.reLaunch({ url: "/pages/dish/list" });
    },
    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr)
        return "";
      return timeStr.replace("T", " ").substring(0, 16);
    },
    // 支付状态文字
    getPayStatusText(payStatus) {
      const payStatusMap = {
        1: "待支付",
        2: "已支付"
      };
      return payStatusMap[payStatus] || "未知状态";
    },
    // 支付状态样式类
    getPayStatusClass(payStatus) {
      switch (payStatus) {
        case 1:
          return "pay-wait";
        case 2:
          return "pay-success";
        default:
          return "";
      }
    },
    // 订单状态文字
    getStatusText(orderStatus) {
      const statusMap = {
        1: "待接单",
        2: "待取餐",
        3: "已完成",
        4: "已取消"
      };
      return statusMap[orderStatus] || "未知状态";
    },
    // 订单状态样式类
    getStatusClass(orderStatus) {
      switch (orderStatus) {
        case 1:
          return "order-wait";
        case 2:
          return "order-processing";
        case 3:
          return "order-success";
        case 4:
          return "order-cancel";
        default:
          return "";
      }
    },
    // 去支付
    goToPay(orderId) {
      const strOrderId = String(orderId);
      common_vendor.index.navigateTo({
        url: `/pages/order/confirm?orderId=${strOrderId}&from=unpaid`
      });
    },
    // 取消订单
    cancelOrder(orderId) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定取消该订单吗？",
        success: async (res) => {
          if (res.confirm) {
            common_vendor.index.showToast({ title: "订单已取消", icon: "success" });
            this.getOrderData();
          }
        }
      });
    },
    // 3. 跳转到评价页面
    goToEvaluation(orderId, item) {
      const strOrderId = String(orderId);
      if (item && item.isEvaluated) {
        common_vendor.index.navigateTo({
          url: `/pages/evaluation/list?orderId=${strOrderId}`
        });
        return;
      }
      common_vendor.index.navigateTo({
        url: `/pages/order/evaluation?orderId=${strOrderId}`
      });
    },
    // 4. 跳转到菜品评价页面
    goToDishEvaluation(orderId) {
      const strOrderId = String(orderId);
      common_vendor.index.navigateTo({
        url: `/pages/order/dish-evaluation?orderId=${strOrderId}`
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
    a: $data.orderList.length === 0 && !$data.loading
  }, $data.orderList.length === 0 && !$data.loading ? {
    b: common_vendor.o((...args) => $options.goToDish && $options.goToDish(...args))
  } : !$data.loading ? {
    d: common_vendor.f($data.orderList, (item, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.orderNo),
        b: common_vendor.t($options.getPayStatusText(item.payStatus)),
        c: common_vendor.n($options.getPayStatusClass(item.payStatus)),
        d: common_vendor.t($options.getStatusText(item.orderStatus)),
        e: common_vendor.n($options.getStatusClass(item.orderStatus)),
        f: common_vendor.t(item.diningType === 1 ? "外带" : "自取"),
        g: common_vendor.t(item.receiverName),
        h: common_vendor.t(item.receiverPhone),
        i: common_vendor.t(item.receiverAddress),
        j: common_vendor.t($options.formatTime(item.createTime)),
        k: common_vendor.t((item.totalAmount || 0).toFixed(2)),
        l: item.payStatus === 1
      }, item.payStatus === 1 ? {
        m: common_vendor.o(($event) => $options.goToPay(item.id), item.id),
        n: common_vendor.o(($event) => $options.cancelOrder(item.id), item.id)
      } : item.payStatus === 2 && item.orderStatus === 2 ? {} : item.orderStatus === 3 ? common_vendor.e({
        q: !item.isEvaluating
      }, !item.isEvaluating ? {
        r: common_vendor.t(item.isEvaluated ? "查看评价" : "去评价"),
        s: item.isEvaluated ? 1 : "",
        t: common_vendor.o(($event) => $options.goToEvaluation(item.id, item), item.id),
        v: common_vendor.o(($event) => $options.goToDishEvaluation(item.id), item.id)
      } : {}) : item.orderStatus === 4 ? {} : {}, {
        o: item.payStatus === 2 && item.orderStatus === 2,
        p: item.orderStatus === 3,
        w: item.orderStatus === 4,
        x: item.id
      });
    })
  } : {}, {
    c: !$data.loading,
    e: $data.loading
  }, $data.loading ? {} : {}, {
    f: common_vendor.p({
      ["current-index"]: 2
    })
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-456ecf67"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/list.js.map
