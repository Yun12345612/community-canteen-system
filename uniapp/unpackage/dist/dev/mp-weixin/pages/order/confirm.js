"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const utils_cartManager = require("../../utils/cartManager.js");
getApp();
const _sfc_main = {
  data() {
    return {
      cartList: [],
      totalPrice: "0.00",
      eatType: 1,
      // 1-外带，2-自取
      remark: "",
      userId: common_vendor.index.getStorageSync("userId") || 1,
      receiverName: "",
      receiverPhone: "",
      receiverAddress: "",
      isSubmitting: false,
      showPayModal: false,
      // 支付弹窗显示状态
      pwd: "",
      // 模拟输入的密码
      currentOrderId: null,
      // 保存订单ID
      fromUnpaid: false,
      // 是否从待支付订单跳转
      animationData: {}
      // 弹窗动画
    };
  },
  async onLoad(options) {
    common_vendor.index.__f__("log", "at pages/order/confirm.vue:111", "接收的参数：", options);
    if (options.orderId && options.from === "unpaid") {
      this.fromUnpaid = true;
      this.currentOrderId = String(options.orderId);
      common_vendor.index.__f__("log", "at pages/order/confirm.vue:117", "待支付订单ID（字符串）：", this.currentOrderId);
      await this.loadUnpaidOrderInfo(this.currentOrderId);
      setTimeout(() => {
        this.showPayModal = true;
        this.pwd = "";
        this.animation.translateY(0).step();
        this.animationData = this.animation.export();
      }, 500);
      return;
    }
    await this.initData();
  },
  onReady() {
    this.animation = common_vendor.index.createAnimation({
      duration: 300,
      timingFunction: "ease-out"
    });
  },
  methods: {
    // 加载购物车数据（正常下单流程）
    async initData() {
      try {
        this.cartList = await utils_cartManager.getCartList(this.userId);
        this.calcTotalPrice();
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:148", "获取购物车数据失败", err);
        common_vendor.index.showToast({ title: "数据加载失败", icon: "none" });
      }
    },
    // 计算总价
    calcTotalPrice() {
      const total = this.cartList.reduce((sum, item) => {
        return sum + (item.price || 0) * item.count;
      }, 0);
      this.totalPrice = total.toFixed(2);
    },
    // 切换就餐方式
    changeType(e) {
      this.eatType = Number(e.detail.value);
    },
    // 提交订单（正常下单流程）
    async submitOrder() {
      if (this.cartList.length === 0) {
        common_vendor.index.showToast({ title: "购物车为空", icon: "none" });
        return;
      }
      if (!this.receiverName.trim()) {
        common_vendor.index.showToast({ title: "请输入收货人姓名", icon: "none" });
        return;
      }
      if (!this.receiverPhone.trim() || !/^1[3-9]\d{9}$/.test(this.receiverPhone)) {
        common_vendor.index.showToast({ title: "请输入正确的联系电话", icon: "none" });
        return;
      }
      if (!this.receiverAddress.trim()) {
        common_vendor.index.showToast({ title: "请输入取餐/收货地址", icon: "none" });
        return;
      }
      this.isSubmitting = true;
      try {
        const params = {
          userId: this.userId,
          receiverName: this.receiverName.trim(),
          receiverPhone: this.receiverPhone.trim(),
          receiverAddress: this.receiverAddress.trim(),
          orderRemark: this.remark.trim(),
          diningType: this.eatType,
          dishItems: this.cartList.map((item) => ({
            dishId: item.dishId,
            count: item.count,
            remark: ""
          }))
        };
        const res = await api_order.submitOrder(params);
        if (res.code !== 200) {
          throw new Error(res.msg || "订单创建失败");
        }
        this.currentOrderId = String(res.data);
        common_vendor.index.__f__("log", "at pages/order/confirm.vue:209", "创建订单成功，订单ID：", this.currentOrderId);
        common_vendor.index.showToast({ title: "订单创建成功", icon: "success" });
        setTimeout(() => {
          this.showPayModal = true;
          this.pwd = "";
          this.animation.translateY(0).step();
          this.animationData = this.animation.export();
        }, 1e3);
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:222", "订单创建失败：", err);
        common_vendor.index.showToast({ title: err.msg || err.message || "订单创建失败", icon: "none" });
      } finally {
        this.isSubmitting = false;
      }
    },
    // 加载待支付订单信息
    async loadUnpaidOrderInfo(orderId) {
      try {
        common_vendor.index.__f__("log", "at pages/order/confirm.vue:232", "查询订单详情，ID：", orderId);
        const timeoutPromise = new Promise((_, reject) => {
          setTimeout(() => reject(new Error("加载订单超时")), 5e3);
        });
        const res = await Promise.race([
          api_order.getOrderDetail(orderId),
          timeoutPromise
        ]);
        const order = res.data;
        if (!order) {
          common_vendor.index.showToast({ title: "订单不存在", icon: "none" });
          common_vendor.index.navigateBack();
          return;
        }
        if (order.payStatus !== 1) {
          common_vendor.index.showToast({ title: "订单非待支付状态", icon: "none" });
          common_vendor.index.navigateBack();
          return;
        }
        this.receiverName = order.receiverName || "";
        this.receiverPhone = order.receiverPhone || "";
        this.receiverAddress = order.receiverAddress || "";
        this.eatType = order.diningType || 1;
        this.remark = order.orderRemark || "";
        this.totalPrice = (order.totalAmount || 0).toFixed(2);
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:264", "加载订单信息失败：", err);
        common_vendor.index.showToast({ title: err.message || "加载订单失败", icon: "none" });
        common_vendor.index.navigateBack();
      }
    },
    // 关闭支付弹窗
    closePayModal() {
      this.animation.translateY("100%").step();
      this.animationData = this.animation.export();
      setTimeout(() => {
        this.showPayModal = false;
        if (this.fromUnpaid) {
          common_vendor.index.navigateBack();
        }
      }, 300);
    },
    // 输入支付密码
    inputPwd(num) {
      if (this.pwd.length >= 6)
        return;
      this.pwd += num;
      if (this.pwd.length === 6) {
        this.handleMockPay();
      }
    },
    // 删除密码
    deletePwd() {
      if (this.pwd.length > 0) {
        this.pwd = this.pwd.substring(0, this.pwd.length - 1);
      }
    },
    // 调用模拟支付接口
    async handleMockPay() {
      common_vendor.index.__f__("log", "at pages/order/confirm.vue:300", "调用支付接口，订单ID：", this.currentOrderId);
      if (!this.currentOrderId) {
        common_vendor.index.showToast({ title: "订单ID为空", icon: "none" });
        return;
      }
      try {
        const res = await api_order.mockPay({
          orderId: String(this.currentOrderId)
        });
        common_vendor.index.__f__("log", "at pages/order/confirm.vue:310", "支付接口返回：", res);
        this.closePayModal();
        if (res.status === "success") {
          if (!this.fromUnpaid) {
            await utils_cartManager.clearCartAfterOrder(this.userId);
          }
          common_vendor.index.redirectTo({
            url: `/pages/order/pay-result?status=success&orderId=${this.currentOrderId}&totalPrice=${this.totalPrice}`
          });
        } else {
          common_vendor.index.redirectTo({
            url: `/pages/order/pay-result?status=fail&orderId=${this.currentOrderId}&totalPrice=${this.totalPrice}`
          });
        }
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:331", "模拟支付失败：", err);
        this.closePayModal();
        common_vendor.index.showToast({ title: "支付异常", icon: "none" });
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.fromUnpaid,
    b: $data.receiverName,
    c: common_vendor.o(($event) => $data.receiverName = $event.detail.value),
    d: $data.fromUnpaid,
    e: $data.receiverPhone,
    f: common_vendor.o(($event) => $data.receiverPhone = $event.detail.value),
    g: $data.fromUnpaid,
    h: $data.receiverAddress,
    i: common_vendor.o(($event) => $data.receiverAddress = $event.detail.value),
    j: $data.eatType === 1,
    k: $data.eatType === 2,
    l: common_vendor.o((...args) => $options.changeType && $options.changeType(...args)),
    m: $data.fromUnpaid,
    n: $data.fromUnpaid,
    o: $data.remark,
    p: common_vendor.o(($event) => $data.remark = $event.detail.value),
    q: !$data.fromUnpaid
  }, !$data.fromUnpaid ? {
    r: common_vendor.f($data.cartList, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.dishName),
        b: common_vendor.t(item.count),
        c: common_vendor.t((item.price * item.count).toFixed(2)),
        d: item.id
      };
    })
  } : {}, {
    s: common_vendor.t($data.totalPrice),
    t: !$data.fromUnpaid
  }, !$data.fromUnpaid ? {
    v: common_vendor.o((...args) => $options.submitOrder && $options.submitOrder(...args)),
    w: $data.isSubmitting
  } : {}, {
    x: $data.showPayModal
  }, $data.showPayModal ? {
    y: common_vendor.o((...args) => $options.closePayModal && $options.closePayModal(...args))
  } : {}, {
    z: $data.showPayModal
  }, $data.showPayModal ? {
    A: common_vendor.o((...args) => $options.closePayModal && $options.closePayModal(...args)),
    B: common_vendor.t($data.totalPrice),
    C: common_vendor.f(6, (i, k0, i0) => {
      return common_vendor.e({
        a: $data.pwd.length >= i
      }, $data.pwd.length >= i ? {} : {}, {
        b: i
      });
    }),
    D: common_vendor.f([1, 2, 3, 4, 5, 6, 7, 8, 9], (num, k0, i0) => {
      return {
        a: common_vendor.t(num),
        b: num,
        c: common_vendor.o(($event) => $options.inputPwd(num), num)
      };
    }),
    E: common_vendor.o(($event) => $options.inputPwd(0)),
    F: common_vendor.o((...args) => $options.deletePwd && $options.deletePwd(...args)),
    G: $data.animationData
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-324e7894"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/confirm.js.map
