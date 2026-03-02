"use strict";
const common_vendor = require("../../common/vendor.js");
const api_evaluation = require("../../api/evaluation.js");
const _sfc_main = {
  data() {
    return {
      userId: "",
      loading: false,
      rawData: "",
      evaluationList: [],
      error: ""
    };
  },
  onLoad() {
    this.userId = common_vendor.index.getStorageSync("userId") || "未登录";
  },
  methods: {
    async loadData() {
      this.loading = true;
      this.error = "";
      this.rawData = "";
      this.evaluationList = [];
      try {
        common_vendor.index.__f__("log", "at pages/evaluation/debug.vue:93", "【调试】开始加载评价数据...");
        common_vendor.index.__f__("log", "at pages/evaluation/debug.vue:94", "【调试】用户ID:", this.userId);
        const res = await api_evaluation.getUserEvaluations();
        common_vendor.index.__f__("log", "at pages/evaluation/debug.vue:98", "【调试】接口响应:", res);
        this.rawData = JSON.stringify(res, null, 2);
        if (res.code === 200) {
          this.evaluationList = res.data || [];
          common_vendor.index.__f__("log", "at pages/evaluation/debug.vue:105", "【调试】评价列表:", this.evaluationList);
          this.evaluationList.forEach((item, index) => {
            common_vendor.index.__f__("log", "at pages/evaluation/debug.vue:109", `【调试】评价 ${index + 1}:`, {
              id: item.id,
              orderNo: item.orderNo,
              dishEvaluationsCount: item.dishEvaluations ? item.dishEvaluations.length : 0,
              dishEvaluations: item.dishEvaluations
            });
          });
        } else {
          this.error = res.msg || "获取数据失败";
        }
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/evaluation/debug.vue:120", "【调试】加载失败:", err);
        this.error = err.message || "网络错误";
      } finally {
        this.loading = false;
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.t($data.userId),
    b: common_vendor.o((...args) => $options.loadData && $options.loadData(...args)),
    c: $data.loading
  }, $data.loading ? {} : {}, {
    d: $data.rawData
  }, $data.rawData ? {
    e: common_vendor.t($data.rawData)
  } : {}, {
    f: $data.evaluationList.length > 0
  }, $data.evaluationList.length > 0 ? {
    g: common_vendor.t($data.evaluationList.length),
    h: common_vendor.f($data.evaluationList, (item, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.id),
        b: common_vendor.t(item.orderNo),
        c: common_vendor.t(item.score),
        d: common_vendor.t(item.content),
        e: common_vendor.t(item.dishEvaluations ? item.dishEvaluations.length : 0),
        f: item.dishEvaluations && item.dishEvaluations.length > 0
      }, item.dishEvaluations && item.dishEvaluations.length > 0 ? {
        g: common_vendor.f(item.dishEvaluations, (dish, dIndex, i1) => {
          return {
            a: common_vendor.t(dIndex + 1),
            b: common_vendor.t(dish.dishName),
            c: common_vendor.t(dish.score),
            d: common_vendor.t(dish.content),
            e: dIndex
          };
        })
      } : {}, {
        h: index
      });
    })
  } : {}, {
    i: $data.error
  }, $data.error ? {
    j: common_vendor.t($data.error)
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-f9702387"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/evaluation/debug.js.map
