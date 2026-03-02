"use strict";
const common_vendor = require("../common/vendor.js");
const api_cart = require("../api/cart.js");
const utils_request = require("./request.js");
async function addCart(param1, param2, param3) {
  let params = {};
  if (typeof param1 === "object" && param1 !== null) {
    const userId = common_vendor.index.getStorageSync("userId") || 1;
    params = {
      userId,
      dishId: param1.id,
      count: 1
    };
  } else {
    params = {
      userId: param1,
      dishId: param2,
      count: param3 || 1
    };
  }
  if (!params.userId || !params.dishId || params.count <= 0) {
    common_vendor.index.showToast({ title: "参数异常，操作失败", icon: "none" });
    return Promise.reject(new Error("参数异常"));
  }
  try {
    const res = await api_cart.addCartApi(params);
    common_vendor.index.showToast({
      title: res.msg || "加入购物车成功",
      icon: "success"
    });
    return res;
  } catch (err) {
    common_vendor.index.__f__("error", "at utils/cartManager.js:37", "加入购物车失败：", err);
    return Promise.reject(err);
  }
}
async function getCartList(userId) {
  if (!userId) {
    common_vendor.index.showToast({ title: "用户ID不能为空", icon: "none" });
    return [];
  }
  try {
    const res = await api_cart.getCartListApi({ userId });
    return res.data || [];
  } catch (err) {
    common_vendor.index.__f__("error", "at utils/cartManager.js:53", "获取购物车列表失败：", err);
    return [];
  }
}
async function updateCartCount(userId, dishId, count) {
  if (!userId || !dishId || count <= 0) {
    common_vendor.index.showToast({ title: "数量必须大于0", icon: "none" });
    return Promise.reject(new Error("参数异常"));
  }
  try {
    const res = await api_cart.updateCartCountApi({ userId, dishId, count });
    common_vendor.index.showToast({
      title: res.msg || "数量更新成功",
      icon: "success"
    });
    return res;
  } catch (err) {
    common_vendor.index.__f__("error", "at utils/cartManager.js:71", "更新数量失败：", err);
    return Promise.reject(err);
  }
}
async function getCartTotalCount(userId) {
  if (!userId)
    return 0;
  try {
    const cartList = await getCartList(userId);
    return cartList.reduce((sum, item) => sum + (item.count || 0), 0);
  } catch (e) {
    common_vendor.index.__f__("error", "at utils/cartManager.js:100", "获取购物车总数失败", e);
    return 0;
  }
}
const clearCartAfterOrder = async (userId) => {
  if (!userId) {
    common_vendor.index.showToast({ title: "用户ID不能为空", icon: "none" });
    return Promise.reject(new Error("参数异常"));
  }
  try {
    const res = await utils_request.request({
      url: "/cart/clearAfterOrder",
      method: "POST",
      data: { userId }
    });
    common_vendor.index.showToast({ title: "购物车已清空", icon: "success" });
    return res;
  } catch (err) {
    common_vendor.index.__f__("error", "at utils/cartManager.js:122", "清空购物车失败：", err);
    common_vendor.index.showToast({ title: "清空购物车失败", icon: "none" });
    return Promise.reject(err);
  }
};
exports.addCart = addCart;
exports.clearCartAfterOrder = clearCartAfterOrder;
exports.getCartList = getCartList;
exports.getCartTotalCount = getCartTotalCount;
exports.updateCartCount = updateCartCount;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/cartManager.js.map
