// src/stores/merchant.js
import { defineStore } from 'pinia'

// 定义商家登录态仓库，与菜品页面导入的 useMerchantStore 对应
export const useMerchantStore = defineStore('merchant', {
  // 状态：存储商家登录信息
  state: () => ({
    // 从localStorage初始化（登录后会存入，与路由守卫的缓存一致）
    merchantInfo: JSON.parse(localStorage.getItem('merchantInfo') || '{}')
  }),
  // 方法：修改状态（存储/清除登录信息）
  actions: {
    // 存储商家登录信息（登录成功后调用）
    setMerchantInfo(info) {
      this.merchantInfo = info
      // 同步到localStorage，刷新页面不丢失
      localStorage.setItem('merchantInfo', JSON.stringify(info))
    },
    // 清除登录信息（退出登录时调用）
    clearMerchantInfo() {
      this.merchantInfo = {}
      localStorage.removeItem('merchantInfo')
    }
  }
})