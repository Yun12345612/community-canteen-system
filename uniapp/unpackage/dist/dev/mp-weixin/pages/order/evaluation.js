"use strict";
const common_vendor = require("../../common/vendor.js");
const api_evaluation = require("../../api/evaluation.js");
const _sfc_main = {
  data() {
    return {
      orderId: "",
      userId: common_vendor.index.getStorageSync("userId") || "",
      loading: false,
      submitting: false,
      orderScore: 5,
      totalContent: "",
      imgUrls: []
    };
  },
  onLoad(options) {
    setTimeout(async () => {
      this.orderId = options.orderId || "";
      if (!this.userId) {
        common_vendor.index.showToast({ title: "请先登录后再评价", icon: "none", duration: 2e3 });
        setTimeout(() => this.safeNavigateBack(), 500);
        return;
      }
      if (!this.orderId) {
        common_vendor.index.showToast({ title: "订单ID不能为空", icon: "none", duration: 2e3 });
        setTimeout(() => this.safeNavigateBack(), 500);
        return;
      }
      this.loading = false;
    }, 100);
  },
  methods: {
    safeNavigateBack() {
      try {
        const pages = getCurrentPages();
        if (pages.length > 1) {
          common_vendor.index.navigateBack({ delta: 1, fail: () => common_vendor.index.redirectTo({ url: "/pages/index/index" }) });
        } else {
          common_vendor.index.redirectTo({ url: "/pages/index/index" });
        }
      } catch (err) {
        common_vendor.index.redirectTo({ url: "/pages/index/index" });
      }
    },
    setOrderScore(score) {
      this.orderScore = score;
    },
    chooseImage() {
      const remainCount = 3 - this.imgUrls.length;
      if (remainCount <= 0) {
        common_vendor.index.showToast({ title: "最多只能上传3张图片", icon: "none" });
        return;
      }
      common_vendor.index.chooseImage({
        count: remainCount,
        sizeType: ["original", "compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          this.uploadImages(res.tempFilePaths);
        },
        fail: () => {
          common_vendor.index.showToast({ title: "选择图片失败", icon: "none" });
        }
      });
    },
    async uploadImages(tempFilePaths) {
      common_vendor.index.hideLoading();
      common_vendor.index.showLoading({ title: "图片上传中...", mask: true });
      try {
        for (const tempPath of tempFilePaths) {
          const uploadRes = await new Promise((resolve, reject) => {
            common_vendor.index.uploadFile({
              url: "http://127.0.0.1:8080/common/upload",
              filePath: tempPath,
              name: "file",
              success: (res) => {
                try {
                  if (res.statusCode !== 200) {
                    reject(new Error("服务器响应异常"));
                    return;
                  }
                  const resData = JSON.parse(res.data);
                  if (resData.code === 200)
                    resolve(resData.data);
                  else
                    reject(new Error(resData.msg || "图片上传失败"));
                } catch (e) {
                  reject(new Error("解析响应失败"));
                }
              },
              fail: (err) => reject(new Error(err.errMsg || "网络异常"))
            });
          });
          this.imgUrls.push(uploadRes);
        }
        common_vendor.index.showToast({ title: `上传成功${tempFilePaths.length}张`, icon: "success" });
      } catch (err) {
        common_vendor.index.showToast({ title: err.message || "上传失败", icon: "none" });
      } finally {
        common_vendor.index.hideLoading();
      }
    },
    deleteImg(index) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除这张图片吗？",
        success: (res) => {
          if (res.confirm) {
            this.imgUrls.splice(index, 1);
            common_vendor.index.showToast({ title: "删除成功", icon: "success", duration: 1e3 });
          }
        }
      });
    },
    async submitEvaluation() {
      if (!this.totalContent && this.imgUrls.length === 0) {
        common_vendor.index.showToast({ title: "请填写评价内容或上传图片", icon: "none" });
        return;
      }
      this.submitting = true;
      try {
        const params = {
          orderId: this.orderId,
          userId: Number(this.userId),
          score: this.orderScore,
          content: this.totalContent,
          imgUrls: this.imgUrls.length > 0 ? this.imgUrls : null
        };
        await api_evaluation.submitEvaluation(params);
        common_vendor.index.showToast({ title: "评价成功！", icon: "success", duration: 2e3 });
        setTimeout(() => this.safeNavigateBack(), 1500);
      } catch (err) {
        common_vendor.index.showToast({ title: err.msg || "评价提交失败", icon: "none" });
      } finally {
        this.submitting = false;
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.loading
  }, $data.loading ? {} : common_vendor.e({
    b: common_vendor.f(5, (star, starIdx, i0) => {
      return {
        a: starIdx < $data.orderScore ? 1 : "",
        b: starIdx,
        c: common_vendor.o(($event) => $options.setOrderScore(starIdx + 1), starIdx)
      };
    }),
    c: common_vendor.t($data.orderScore),
    d: $data.totalContent,
    e: common_vendor.o(($event) => $data.totalContent = $event.detail.value),
    f: common_vendor.f($data.imgUrls, (img, index, i0) => {
      return {
        a: img,
        b: common_vendor.o(($event) => $options.deleteImg(index), index),
        c: index
      };
    }),
    g: $data.imgUrls.length < 3
  }, $data.imgUrls.length < 3 ? {
    h: common_vendor.o((...args) => $options.chooseImage && $options.chooseImage(...args))
  } : {}, {
    i: common_vendor.t($data.submitting ? "提交中..." : "提交评价"),
    j: common_vendor.o((...args) => $options.submitEvaluation && $options.submitEvaluation(...args)),
    k: $data.submitting
  }));
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-9e35c24e"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/evaluation.js.map
