<template>
  <view class="cart-container">
    <view v-if="cartList.length === 0" class="empty-box">
      <text class="empty-icon">🛒</text>
      <text class="empty-text">购物车为空，快去挑选菜品吧~</text>
      <button class="goto-btn" @click="goToDish">立即点餐</button>
    </view>
    <view v-else>
      <view class="cart-item" v-for="item in cartList" :key="item.id">
        <image class="item-img" :src="item.coverImg || '/static/logo.png'" mode="aspectFill" @error="item.coverImg = ''"></image>
        <view class="item-info">
          <view class="name">{{ item.dishName }}</view>
          <view class="price">￥{{ (item.price || 0).toFixed(2) }}</view>
        </view>
        <view class="count-box">
          <text class="count-btn" @click="updateCount(item.dishId, item.count - 1)">-</text>
          <text class="count-num">{{ item.count }}</text>
          <text class="count-btn" @click="updateCount(item.dishId, item.count + 1)">+</text>
        </view>
      </view>

      <view class="bottom-bar">
        <view class="total">合计：￥{{ totalPrice }}</view>
        <button class="settle-btn" @click="toConfirm" hover-class="btn-hover">去结算</button>
      </view>
    </view>
  </view>
</template>

<script>
import { getCartList, updateCartCount } from '@/utils/cartManager.js'

export default {
  data() {
    return {
      cartList: [],
      totalPrice: '0.00',
      userId: uni.getStorageSync('userId') || 1
    }
  },
  onShow() {
    this.initData()
  },
  methods: {
    async initData() {
      try {
        this.cartList = await getCartList(this.userId)
        this.calcTotalPrice()
      } catch (err) {
        console.error('获取购物车失败：', err)
        uni.showToast({ title: '购物车加载失败', icon: 'none' })
      }
    },
    async updateCount(dishId, count) {
      if (count < 1) {
        // 如果减到0，通常逻辑是询问是否删除或直接删除
        // 这里保持原逻辑提示，若需删除功能需额外接口支持
        uni.showToast({ title: '数量不能小于1', icon: 'none' })
        return
      }
      try {
        await updateCartCount(this.userId, dishId, count)
        this.initData()
      } catch (err) {
        console.error('更新数量失败：', err)
        uni.showToast({ title: '操作失败', icon: 'none' })
      }
    },
    toConfirm() {
      if (this.cartList.length === 0) {
        uni.showToast({ title: '请选择商品', icon: 'none' })
        return
      }
      uni.navigateTo({ url: '/pages/order/confirm' })
    },
    goToDish() {
       uni.reLaunch({ url: '/pages/dish/list' })
    },
    calcTotalPrice() {
      const total = this.cartList.reduce((sum, item) => {
        return sum + (item.price || 0) * item.count
      }, 0)
      this.totalPrice = total.toFixed(2)
    }
  }
}
</script>

<style scoped>
/* --- 全局容器 --- */
.cart-container {
  padding: 24rpx;
  background: #f8fafc; /* 浅灰白背景 */
  min-height: 100vh;
  padding-bottom: 140rpx;
  box-sizing: border-box;
}

/* --- 空状态优化 --- */
.empty-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
  animation: fadeIn 0.5s ease;
}

.empty-icon {
  font-size: 120rpx;
  display: block;
  margin-bottom: 30rpx;
  /* 使用登录页同款绿色 #07c160 */
  color: #07c160; 
  filter: drop-shadow(0 10rpx 20rpx rgba(7, 193, 96, 0.2));
  transition: transform 0.3s;
}

.empty-text {
  font-size: 30rpx;
  color: #64748b;
  display: block;
  margin-bottom: 60rpx;
  letter-spacing: 1rpx;
  font-weight: 400;
}

.goto-btn {
  width: 280rpx;
  height: 80rpx;
  /*使用登录页同款绿色渐变 #07c160 -> #02b454 */
  background: linear-gradient(135deg, #07c160 0%, #02b454 100%);
  color: #fff;
  border: none;
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: 600;
  /* 阴影颜色同步 */
  box-shadow: 0 8rpx 20rpx rgba(7, 193, 96, 0.3);
  transition: transform 0.2s;
}

.goto-btn:active {
  transform: scale(0.95);
}

/* --- 列表项优化 --- */
.cart-item {
  display: flex;
  align-items: center;
  background: #ffffff;
  padding: 20rpx;
  border-radius: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  transition: transform 0.2s, box-shadow 0.2s;
}

.cart-item:active {
  transform: scale(0.98);
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.02);
}

.item-img {
  width: 140rpx;
  height: 140rpx;
  border-radius: 16rpx;
  margin-right: 24rpx;
  object-fit: cover;
  background: #f1f5f9;
  border: 1rpx solid #e2e8f0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}

.name {
  font-size: 32rpx;
  color: #1e293b;
  margin-bottom: 12rpx;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.price {
  font-size: 34rpx;
  color: #ff5000; /* 价格保留暖色 */
  font-weight: 700;
  font-family: DIN, 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.price::before {
  content: '￥';
  font-size: 24rpx;
  margin-right: 4rpx;
  font-weight: 500;
}

/* --- 计数器优化 --- */
.count-box {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: #f1f5f9;
  padding: 8rpx;
  border-radius: 30rpx;
}

.count-btn {
  width: 56rpx;
  height: 56rpx;
  line-height: 56rpx;
  text-align: center;
  background: #ffffff;
  border-radius: 50%;
  font-size: 32rpx;
  /* 按钮文字使用登录页同款绿色 */
  color: #07c160;
  font-weight: bold;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
  transition: all 0.2s;
  user-select: none;
}

.count-btn:active {
  /* 按下背景使用登录页同款深绿 */
  background: #07c160;
  color: #fff;
  transform: scale(0.9);
}

.count-num {
  font-size: 30rpx;
  min-width: 48rpx;
  text-align: center;
  font-weight: 600;
  color: #1e293b;
}

/* --- 底部栏优化 --- */
.bottom-bar {
  position: fixed;
  bottom: 30rpx;
  left: 24rpx;
  right: 24rpx;
  height: 100rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20rpx);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx;
  border-radius: 50rpx;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.08);
  z-index: 999;
  box-sizing: border-box;
  border: 1rpx solid rgba(226, 232, 240, 0.6);
}

.total {
  font-size: 36rpx;
  color: #1e293b;
  font-weight: 600;
}

.total::before {
  content: '合计：';
  font-size: 26rpx;
  color: #64748b;
  font-weight: 400;
  margin-right: 8rpx;
}

.settle-btn {
  width: 220rpx;
  height: 72rpx;
  /* 结算按钮使用登录页同款绿色渐变 */
  background: linear-gradient(90deg, #07c160 0%, #02b454 100%);
  color: #fff;
  border: none;
  border-radius: 36rpx;
  font-size: 30rpx;
  font-weight: 600;
  /* 阴影颜色同步 */
  box-shadow: 0 8rpx 20rpx rgba(7, 193, 96, 0.4);
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-hover {
  opacity: 1;
  transform: scale(0.96);
  box-shadow: 0 4rpx 10rpx rgba(7, 193, 96, 0.3);
}

/* 动画定义 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
