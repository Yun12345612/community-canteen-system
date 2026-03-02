"use strict";
const common_vendor = require("../../common/vendor.js");
const api_user = require("../../api/user.js");
const _sfc_main = {
  __name: "login",
  setup(__props) {
    const handleWechatLogin = async () => {
      try {
        const loginRes = await common_vendor.index.login({ provider: "weixin" });
        const code = loginRes.code;
        if (!code) {
          common_vendor.index.showToast({ title: "获取凭证失败", icon: "none" });
          return;
        }
        const res = await api_user.wechatLogin(code);
        common_vendor.index.setStorageSync("token", res.data.token);
        common_vendor.index.setStorageSync("userInfo", res.data.userInfo);
        common_vendor.index.setStorageSync("userId", res.data.userInfo.id || res.data.userId);
        common_vendor.index.showToast({ title: "登录成功", icon: "success" });
        setTimeout(() => {
          common_vendor.index.reLaunch({ url: "/pages/index/index" });
        }, 1500);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/login/login.vue:68", "登录异常：", error);
        common_vendor.index.showToast({ title: "登录失败，请重试", icon: "none" });
      }
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.o(handleWechatLogin)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
