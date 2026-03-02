import { addCartApi, getCartListApi, updateCartCountApi, clearCartApi } from '@/api/cart.js'
import request from '../utils/request.js'

/**
 * 加入购物车（兼容对象/参数调用）
 */
export async function addCart(param1, param2, param3) {
  let params = {}
  if (typeof param1 === 'object' && param1 !== null) {
    const userId = uni.getStorageSync('userId') || 1
    params = {
      userId: userId,
      dishId: param1.id,
      count: 1
    }
  } else {
    params = {
      userId: param1,
      dishId: param2,
      count: param3 || 1
    }
  }

  if (!params.userId || !params.dishId || params.count <= 0) {
    uni.showToast({ title: '参数异常，操作失败', icon: 'none' })
    return Promise.reject(new Error('参数异常'))
  }

  try {
    const res = await addCartApi(params)
    uni.showToast({
      title: res.msg || '加入购物车成功',
      icon: 'success'
    })
    return res
  } catch (err) {
    console.error('加入购物车失败：', err)
    return Promise.reject(err)
  }
}

// cartManager.js 中 getCartList 修正
export async function getCartList(userId) {
  if (!userId) {
    uni.showToast({ title: '用户ID不能为空', icon: 'none' })
    return []
  }
  try {
    // 把userId包装成{userId: userId}对象传递
    const res = await getCartListApi({ userId: userId })
    return res.data || []
  } catch (err) {
    console.error('获取购物车列表失败：', err)
    return []
  }
}

export async function updateCartCount(userId, dishId, count) {
  if (!userId || !dishId || count <= 0) {
    uni.showToast({ title: '数量必须大于0', icon: 'none' })
    return Promise.reject(new Error('参数异常'))
  }
  try {
    const res = await updateCartCountApi({ userId, dishId, count })
    uni.showToast({
      title: res.msg || '数量更新成功',
      icon: 'success'
    })
    return res
  } catch (err) {
    console.error('更新数量失败：', err)
    return Promise.reject(err)
  }
}

export async function clearCart(userId) {
  if (!userId) {
    uni.showToast({ title: '用户ID不能为空', icon: 'none' })
    return Promise.reject(new Error('参数异常'))
  }
  try {
    const res = await clearCartApi(userId)
    uni.showToast({
      title: res.msg || '购物车已清空',
      icon: 'success'
    })
    return res
  } catch (err) {
    console.error('清空购物车失败：', err)
    return Promise.reject(err)
  }
}

export async function getCartTotalCount(userId) {
  if (!userId) return 0
  try {
    const cartList = await getCartList(userId)
    return cartList.reduce((sum, item) => sum + (item.count || 0), 0)
  } catch (e) {
    console.error('获取购物车总数失败', e)
    return 0
  }
}
/**
 * 下单后清空购物车（不归还库存）
 * @param {Number} userId - 用户ID
 */
export const clearCartAfterOrder = async (userId) => {
  if (!userId) {
    uni.showToast({ title: '用户ID不能为空', icon: 'none' })
    return Promise.reject(new Error('参数异常'))
  }
  try {
    const res = await request({
      url: "/cart/clearAfterOrder",
      method: "POST",
      data: { userId }
    });
    uni.showToast({ title: '购物车已清空', icon: 'success' })
    return res
  } catch (err) {
    console.error('清空购物车失败：', err)
    uni.showToast({ title: '清空购物车失败', icon: 'none' })
    return Promise.reject(err)
  }
};
