<template>
  <view class="page-container">
    <!-- 顶部分类导航 -->
    <view class="category-wrapper">
      <scroll-view class="category-scroll" scroll-x="true" show-scrollbar="false">
        <view class="category-list">
          <!-- 全部选项 -->
          <view class="category-item" :class="{active: activeCategoryId === 0}" @click="switchCategory(0)">全部</view>
          <!-- 动态分类列表 -->
          <view v-for="item in categoryList" :key="item.id" class="category-item" :class="{active: activeCategoryId === item.id}" @click="switchCategory(item.id)">{{ item.categoryName }}</view>
        </view>
      </scroll-view>
    </view>

    <!-- 菜品列表区域 -->
    <view class="dish-list-wrapper">
      <!-- 空状态 -->
      <view v-if="showDishList.length === 0" class="empty-state">
        <text class="empty-icon">🍽️</text>
        <text class="empty-text">暂无菜品数据，敬请期待</text>
      </view>

      <!-- 菜品卡片 -->
      <view v-else v-for="dish in showDishList" :key="dish.id" class="dish-card" @click="goToDishDetail(dish.id)">
        <image class="dish-img" :src="(dish && dish.coverImg) || '/static/logo.png'" mode="aspectFill" lazy-load @error="dish.coverImg = ''"></image>
        <view class="dish-info">
          <view class="dish-name">{{ dish.dishName }}</view>
          <view class="dish-desc">{{ (dish && dish.description) || '暂无描述' }}</view>
          <view class="dish-price">￥{{ dish.price.toFixed(2) }}</view>
          <view class="dish-status" :class="dish.status === 1 ? 'status-on' : 'status-off'">
            {{ dish.status === 1 ? '上架中' : '已下架' }}
          </view>
        </view>
        <!-- 加入购物车按钮：已改为暖橙色 -->
        <button v-if="dish.status === 1" class="add-cart-btn" @click.stop="addCart(dish)" hover-class="btn-hover">加入购物车</button>
      </view>
    </view>

    <!-- 悬浮购物车按钮：已改为暖橙色 -->
    <view class="cart-float" @click="goToCart" hover-class="cart-hover">
      <text class="cart-icon">🛒</text>
      <text class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</text>
    </view>

    <!-- 底部自定义导航栏 -->
    <custom-tabbar :current-index="1"></custom-tabbar>
  </view>
</template>

<script>
import categoryApi from '../../api/category.js'
import dishApi from '../../api/dish.js'
import { addCart, getCartTotalCount } from '@/utils/cartManager.js'
import customTabbar from "@/components/custom-tabbar/custom-tabbar.vue"

export default {
  components: {
    customTabbar
  },
  data() {
    return {
      categoryList: [],
      allDishList: [],
      activeCategoryId: 0,
      showDishList: [],
      cartCount: 0,
      userId: uni.getStorageSync('userId') || 1
    }
  },
  onShow() {
    this.loadCartCount()
  },
  onLoad() {
    this.getCategoryData()
    this.getDishData()
  },
  methods: {
    async getCategoryData() {
      try {
        const res = await categoryApi.getValidCategoryList()
        this.categoryList = res?.data || []
      } catch (err) {
        console.error('获取分类失败：', err)
        uni.showToast({ title: '分类加载失败', icon: 'none' })
      }
    },
    async getDishData() {
      try {
        const res = await dishApi.getDishList()
        this.allDishList = res?.data || []
        this.showDishList = this.allDishList
      } catch (err) {
        console.error('获取菜品失败：', err)
        uni.showToast({ title: '菜品加载失败', icon: 'none' })
      }
    },
    switchCategory(id) {
      this.activeCategoryId = id
      this.showDishList = id === 0 ? this.allDishList : this.allDishList.filter(item => item?.categoryId === id)
    },
    async loadCartCount() {
      this.cartCount = await getCartTotalCount(this.userId)
    },
    async addCart(dish) {
      if (dish.status !== 1) {
        uni.showToast({ title: '菜品已下架', icon: 'none' })
        return
      }
      try {
        await addCart(this.userId, dish.id, 1)
        await this.loadCartCount()
        uni.showToast({ title: '加入购物车成功', icon: 'success' })
      } catch (err) {
        console.error('加入购物车失败：', err)
        uni.showToast({ title: '库存不足', icon: 'none' })
      }
    },
    goToCart() {
      uni.navigateTo({ url: '/pages/cart/cart' })
    },
    goToDishDetail(dishId) {
      uni.navigateTo({
        url: `/pages/dish/detail?dishId=${dishId}`
      })
    }
  }
}
</script>

<style scoped>
/* --- 全局容器 --- */
.page-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f8fafc; /* 清新浅灰白背景 */
  padding-bottom: 140rpx;
  box-sizing: border-box;
}

/* --- 分类导航栏 --- */
.category-wrapper {
  background: #ffffff;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.02);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1rpx solid #f1f5f9;
}

.category-scroll {
  height: 96rpx;
}

.category-list {
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  box-sizing: border-box;
  gap: 10rpx;
}

.category-item {
  padding: 0 28rpx;
  height: 96rpx;
  line-height: 96rpx;
  font-size: 30rpx;
  color: #64748b;
  white-space: nowrap;
  position: relative;
  transition: all 0.3s ease;
  font-weight: 500;
}

/* 选中态：保持品牌绿 */
.category-item.active {
  color: #10b981;
  font-weight: 700;
  transform: scale(1.05);
}

.category-item.active::after {
  content: "";
  position: absolute;
  bottom: 16rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 32rpx;
  height: 6rpx;
  background: linear-gradient(90deg, #10b981, #34d399);
  border-radius: 3rpx;
  box-shadow: 0 2rpx 8rpx rgba(16, 185, 129, 0.4);
}

/* --- 菜品列表区域 --- */
.dish-list-wrapper {
  padding: 24rpx;
  box-sizing: border-box;
}

.dish-card {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 24rpx;
  padding: 20rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
  border: 1rpx solid rgba(255,255,255,0.6);
  transition: transform 0.2s, box-shadow 0.2s;
}

.dish-card:active {
  transform: scale(0.98);
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.02);
}

.dish-img {
  width: 160rpx;
  height: 160rpx;
  border-radius: 16rpx;
  object-fit: cover;
  flex-shrink: 0;
  background-color: #f1f5f9;
  border: 1rpx solid #e2e8f0;
}

.dish-info {
  flex: 1;
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}

.dish-name {
  font-size: 32rpx;
  color: #1e293b;
  font-weight: 700;
  margin-bottom: 8rpx;
  line-height: 1.3;
}

.dish-desc {
  font-size: 24rpx;
  color: #94a3b8;
  margin-bottom: 12rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.dish-price {
  font-size: 34rpx;
  color: #ff5000; /* 价格暖色强调 */
  font-weight: 800;
  margin-bottom: 12rpx;
  font-family: DIN, 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.dish-price::before {
  content: '￥';
  font-size: 24rpx;
  margin-right: 4rpx;
  font-weight: 600;
}

.dish-status {
  display: inline-block;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: 600;
  width: fit-content;
}

/* 状态标签：保持绿色系 */
.status-on {
  color: #10b981;
  background-color: #ecfdf5;
  border: 1rpx solid #a7f3d0;
}

.status-off {
  color: #94a3b8;
  background-color: #f1f5f9;
  border: 1rpx solid #e2e8f0;
}

/* --- 加入购物车按钮：已改为暖橙色渐变 --- */
.add-cart-btn {
  width: 170rpx;
  height: 64rpx;
  line-height: 64rpx;
  font-size: 26rpx;
  font-weight: 600;
  /* 暖橙色渐变，刺激购买 */
  background: linear-gradient(135deg, #ff7b00 0%, #ff9100 100%);
  color: #fff;
  border: none;
  border-radius: 32rpx;
  flex-shrink: 0;
  padding: 0;
  box-shadow: 0 6rpx 16rpx rgba(255, 123, 0, 0.3);
  transition: all 0.2s;
  margin-left: 10rpx;
}

.add-cart-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2rpx 8rpx rgba(255, 123, 0, 0.2);
}

.btn-hover {
  opacity: 1;
}

/* --- 空状态 --- */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 0;
  color: #94a3b8;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
  opacity: 0.8;
  filter: grayscale(0.2);
}

.empty-text {
  font-size: 30rpx;
  font-weight: 500;
}

/* --- 悬浮购物车按钮--- */
.cart-float {
  position: fixed;
  right: 30rpx;
  bottom: 140rpx;
  width: 100rpx;
  height: 100rpx;
  /* 暖橙色渐变球体 */
  background: linear-gradient(135deg, #ff6a00 0%, #ff9500 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  box-shadow: 0 8rpx 24rpx rgba(255, 149, 0, 0.4);
  transition: transform 0.2s;
  border: 2rpx solid rgba(255,255,255,0.2);
}

.cart-float:active {
  transform: scale(0.9);
}

.cart-hover {
  transform: scale(0.9);
  opacity: 1;
}

.cart-icon {
  color: #fff;
  font-size: 48rpx;
  margin-top: -4rpx;
}

.cart-badge {
  position: absolute;
  top: -4rpx;
  right: -4rpx;
  min-width: 40rpx;
  height: 40rpx;
  background: #ef4444;
  color: #fff;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: 700;
  text-align: center;
  line-height: 40rpx;
  padding: 0 8rpx;
  box-sizing: border-box;
  border: 2rpx solid #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(239, 68, 68, 0.4);
}

@keyframes popIn {
  from { transform: scale(0); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.cart-badge {
  animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
</style>
