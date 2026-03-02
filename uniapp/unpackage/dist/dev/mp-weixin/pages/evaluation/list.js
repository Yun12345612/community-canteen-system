"use strict";
const common_vendor = require("../../common/vendor.js");
const api_evaluation = require("../../api/evaluation.js");
const _sfc_main = {
  data() {
    return {
      list: [],
      loading: false,
      orderId: ""
    };
  },
  onLoad(options) {
    this.orderId = options.orderId || "";
  },
  onShow() {
    this.load();
  },
  methods: {
    async load() {
      this.loading = true;
      try {
        const res = await api_evaluation.getUserEvaluations();
        common_vendor.index.__f__("log", "at pages/evaluation/list.vue:116", "【评价列表】接口返回数据:", res);
        this.list = (res.data || []).map((x) => {
          var _a;
          common_vendor.index.__f__("log", "at pages/evaluation/list.vue:118", "【评价项】评价ID:", x.id, "菜品评价数量:", ((_a = x.dishEvaluations) == null ? void 0 : _a.length) || 0);
          if (x.dishEvaluations && x.dishEvaluations.length > 0) {
            common_vendor.index.__f__("log", "at pages/evaluation/list.vue:120", "【菜品评价】", x.dishEvaluations);
          }
          return {
            ...x,
            orderId: x.orderId ? String(x.orderId) : ""
          };
        });
        common_vendor.index.__f__("log", "at pages/evaluation/list.vue:127", "【评价列表】处理后的数据:", this.list);
      } catch (e) {
        common_vendor.index.__f__("error", "at pages/evaluation/list.vue:129", "【评价列表】加载失败:", e);
        common_vendor.index.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    parseImageUrls(imgUrls) {
      if (!imgUrls)
        return [];
      if (Array.isArray(imgUrls))
        return imgUrls;
      try {
        const s = String(imgUrls).trim();
        if (!s)
          return [];
        if (s.startsWith("["))
          return JSON.parse(s);
        return s.split(",").map((x) => x.trim()).filter(Boolean);
      } catch (e) {
        return [];
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.loading
  }, $data.loading ? {} : common_vendor.e({
    b: $data.list.length === 0
  }, $data.list.length === 0 ? {} : {
    c: common_vendor.f($data.list, (item, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.orderNo || "--"),
        b: common_vendor.f(5, (n, k1, i1) => {
          return {
            a: n,
            b: n <= (item.score || 0) ? 1 : ""
          };
        }),
        c: common_vendor.t(item.score || 0),
        d: common_vendor.t(item.content || "未填写"),
        e: $options.parseImageUrls(item.imgUrls).length
      }, $options.parseImageUrls(item.imgUrls).length ? {
        f: common_vendor.f($options.parseImageUrls(item.imgUrls), (img, idx, i1) => {
          return {
            a: idx,
            b: img
          };
        })
      } : {}, {
        g: item.dishEvaluations && item.dishEvaluations.length
      }, item.dishEvaluations && item.dishEvaluations.length ? {
        h: common_vendor.t(item.dishEvaluations.length),
        i: common_vendor.f(item.dishEvaluations, (d, k1, i1) => {
          return {
            a: common_vendor.t(d.dishName || "菜品"),
            b: common_vendor.f(5, (n, k2, i2) => {
              return {
                a: n,
                b: n <= (d.score || 0) ? 1 : ""
              };
            }),
            c: common_vendor.t(d.score || 0),
            d: common_vendor.t(d.content || "未填写评价"),
            e: d.id || d.dishId
          };
        })
      } : {}, {
        j: common_vendor.t(item.merchantReply || "商家暂未回复"),
        k: !item.merchantReply ? 1 : "",
        l: item.id,
        m: $data.orderId && item.orderId === $data.orderId ? 1 : ""
      });
    })
  }));
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-bdacd5e0"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/evaluation/list.js.map
