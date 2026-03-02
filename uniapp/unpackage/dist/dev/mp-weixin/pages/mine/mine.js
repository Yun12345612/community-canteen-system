"use strict";
const common_vendor = require("../../common/vendor.js");
const api_user = require("../../api/user.js");
const api_common = require("../../api/common.js");
if (!Math) {
  (uniPopup + customTabbar)();
}
const uniPopup = () => "../../node-modules/@dcloudio/uni-ui/lib/uni-popup/uni-popup.js";
const customTabbar = () => "../../components/custom-tabbar/custom-tabbar.js";
const _sfc_main = {
  __name: "mine",
  setup(__props) {
    const userInfo = common_vendor.ref({ id: "", avatar: "", nickname: "", signature: "" });
    const editForm = common_vendor.ref({ nickname: "", signature: "" });
    const nicknamePopup = common_vendor.ref(null);
    const signaturePopup = common_vendor.ref(null);
    common_vendor.onShow(() => {
      loadUserInfo();
    });
    const loadUserInfo = () => {
      const cacheUser = common_vendor.index.getStorageSync("userInfo");
      if (cacheUser)
        userInfo.value = { ...userInfo.value, ...cacheUser };
    };
    const chooseAvatar = async () => {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: async (res) => {
          const tempPath = res.tempFilePaths[0];
          common_vendor.index.showLoading({ title: "上传中..." });
          try {
            const uploadRes = await api_common.uploadFile(tempPath);
            userInfo.value.avatar = uploadRes.data;
            common_vendor.index.setStorageSync("userInfo", userInfo.value);
            common_vendor.index.showToast({ title: "头像更新成功", icon: "success" });
          } catch (err) {
            common_vendor.index.showToast({ title: "上传失败", icon: "none" });
          } finally {
            common_vendor.index.hideLoading();
          }
        }
      });
    };
    const editNickname = () => {
      editForm.value.nickname = userInfo.value.nickname || "";
      nicknamePopup.value.open();
    };
    const closeNicknamePopup = () => nicknamePopup.value.close();
    const saveNickname = async () => {
      const newName = editForm.value.nickname.trim();
      if (!newName)
        return common_vendor.index.showToast({ title: "昵称不能为空", icon: "none" });
      try {
        await api_user.updateUserInfo({ id: userInfo.value.id, nickname: newName });
        userInfo.value.nickname = newName;
        common_vendor.index.setStorageSync("userInfo", userInfo.value);
        closeNicknamePopup();
        common_vendor.index.showToast({ title: "修改成功", icon: "success" });
      } catch (err) {
        common_vendor.index.showToast({ title: "修改失败", icon: "none" });
      }
    };
    const editSignature = () => {
      editForm.value.signature = userInfo.value.signature || "";
      signaturePopup.value.open();
    };
    const closeSignaturePopup = () => signaturePopup.value.close();
    const saveSignature = async () => {
      try {
        await api_user.updateUserInfo({ id: userInfo.value.id, signature: editForm.value.signature });
        userInfo.value.signature = editForm.value.signature;
        common_vendor.index.setStorageSync("userInfo", userInfo.value);
        closeSignaturePopup();
        common_vendor.index.showToast({ title: "修改成功", icon: "success" });
      } catch (err) {
        common_vendor.index.showToast({ title: "修改失败", icon: "none" });
      }
    };
    const toOrderList = () => {
      common_vendor.index.navigateTo({
        url: "/pages/order/list"
      });
    };
    const toEvaluationList = () => {
      common_vendor.index.__f__("log", "at pages/mine/mine.vue:178", "点击了我的评价，准备跳转...");
      common_vendor.index.navigateTo({
        url: "/pages/evaluation/list",
        success: () => {
          common_vendor.index.__f__("log", "at pages/mine/mine.vue:182", "跳转成功");
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/mine/mine.vue:185", "跳转失败:", err);
          common_vendor.index.showToast({ title: "跳转失败: " + JSON.stringify(err), icon: "none" });
        }
      });
    };
    const toAbout = () => common_vendor.index.showToast({ title: "功能开发中", icon: "none" });
    const logout = () => {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定退出登录？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.removeStorageSync("userInfo");
            common_vendor.index.removeStorageSync("token");
            common_vendor.index.reLaunch({ url: "/pages/login/login" });
          }
        }
      });
    };
    return (_ctx, _cache) => {
      return {
        a: userInfo.value.avatar || "/static/default-avatar.png",
        b: common_vendor.o(chooseAvatar),
        c: common_vendor.t(userInfo.value.nickname || "未设置昵称"),
        d: common_vendor.t(userInfo.value.signature || "点击设置个性签名"),
        e: common_vendor.o(editSignature),
        f: common_vendor.t(userInfo.value.nickname || "未设置"),
        g: common_vendor.o(editNickname),
        h: common_vendor.o(toOrderList),
        i: common_vendor.o(toEvaluationList),
        j: common_vendor.o(toAbout),
        k: common_vendor.o(logout),
        l: editForm.value.nickname,
        m: common_vendor.o(($event) => editForm.value.nickname = $event.detail.value),
        n: common_vendor.o(closeNicknamePopup),
        o: common_vendor.o(saveNickname),
        p: common_vendor.sr(nicknamePopup, "7c2ebfa5-0", {
          "k": "nicknamePopup"
        }),
        q: common_vendor.p({
          type: "dialog"
        }),
        r: editForm.value.signature,
        s: common_vendor.o(($event) => editForm.value.signature = $event.detail.value),
        t: common_vendor.o(closeSignaturePopup),
        v: common_vendor.o(saveSignature),
        w: common_vendor.sr(signaturePopup, "7c2ebfa5-1", {
          "k": "signaturePopup"
        }),
        x: common_vendor.p({
          type: "dialog"
        }),
        y: common_vendor.p({
          ["current-index"]: 3
        })
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-7c2ebfa5"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/mine/mine.js.map
