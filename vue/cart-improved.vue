<template>
  <view class="cart-page">
    <!-- 自定义导航栏 -->
    <view class="navbar">
      <view class="navbar-title">购物车</view>
      <view class="navbar-edit" v-if="cartList.length > 0" @click="toggleEditMode">
        {{ editMode ? '完成' : '编辑' }}
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="cartList.length === 0" class="empty-container">
      <image class="empty-image" src="/static/empty-cart.png" mode="aspectFit"></image>
      <view class="empty-title">购物车空空如也</view>
      <view class="empty-desc">快去挑选心仪的菜品吧</view>
      <button class="empty-btn" @click="goToDish" hover-class="btn-hover">
        <text class="btn-icon">🍲</text>
        <text class="btn-text">立即去点餐</text>
      </button>
    </view>

    <!-- 购物车列表 -->
    <view v-else class="cart-content">
      <view class="cart-list">
        <view class="cart-item" v-for="item in cartList" :key="item.id" :class="{'item-editing': editMode}">
          <!-- 选择框（编辑模式显示） -->
          <view class="item-select" v-if="editMode" @click="toggleSelect(item)">
            <view class="select-icon" :class="{'selected': selectedItems.includes(item.dishId)}">
              <text v-if="selectedItems.includes(item.dishId)" class="icon-check">✓</text>
            </view>
          </view>

          <!-- 商品图片 -->
          <view class="item-image-wrapper">
            <image class="item-image" :src="item.coverImg || '/static/default-dish.png'" mode="aspectFill"></image>
            <view v-if="item.count > 1" class="item-badge">{{ item.count }}</view>
          </view>

          <!-- 商品信息 -->
          <view class="item-info">
            <view class="item-name">{{ item.dishName }}</view>
            <view class="item-desc" v-if="item.description">{{ item.description }}</view>
            <view class="item-bottom">
              <view class="item-price">￥{{ (item.price || 0).toFixed(2) }}</view>
              <!-- 编辑模式显示删除按钮 -->
              <view v-if="editMode" class="item-delete" @click="deleteItem(item.dishId)">
                <text class="delete-icon">🗑️</text>
                <text class="delete-text">删除</text>
              </view>
              <!-- 正常模式显示数量选择器 -->
              <view v-else class="item-count">
                <view class="count-btn minus" @click="updateCount(item.dishId, item.count - 1)" :class="{'disabled': item.count <= 1}">-</view>
                <view class="count-num">{{ item.count }}</view>
                <view class="count-btn plus" @click="updateCount(item.dishId, item.count + 1)">+</view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 推荐商品（空位时可添加） -->
      <view class="recommend-section" v-if="!editMode">
        <view class="section-title">猜你喜欢</view>
        <view class="recommend-list">
          <view class="recommend-item" v-for="rec in recommendList" :key="rec.id" @click="addRecommend(rec)">
            <image class="recommend-img" :src="rec.coverImg || '/static/default-dish.png'" mode="aspectFill"></image>
            <view class="recommend-name">{{ rec.name }}</view>
            <view class="recommend-price">￥{{ rec.price.toFixed(2) }}</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部结算栏 -->
    <view v-if="cartList.length > 0" class="bottom-bar" :class="{'edit-mode': editMode}">
      <!-- 编辑模式底部栏 -->
      <view v-if="editMode" class="edit-bar">
        <view class="select-all" @click="toggleSelectAll">
          <view class="select-icon" :class="{'selected': isAllSelected}">
            <text v-if="isAllSelected" class="icon-check">✓</text>
          </view>
          <text class="select-text">全选</text>
        </view>
        <button class="delete-btn" @click="batchDelete" :disabled="selectedItems.length === 0">删除({{ selectedItems.length }})</button>
      </view>

      <!-- 正常模式底部栏 -->
      <view v-else class="settle-bar">
        <view class="total-info">
          <view class="total-label">合计：</view>
          <view class="total-price">￥{{ totalPrice }}</view>
          <view class="total-save" v-if="totalSave > 0">已省￥{{ totalSave.toFixed(2) }}</view>
        </view>
        <button class="settle-btn" @click="toConfirm" :disabled="cartList.length === 0" hover-class="btn-hover">
          去结算({{ totalCount }})
        </button>
      </view>
    </view>

    <!-- 加载动画 -->
    <view v-if="loading" class="loading-mask">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <view class="loading-text">加载中...</view>
      </view>
    </view>
  </view>
</template>

<script>
import { getCartList, updateCartCount, deleteCartItem } from '@/utils/cartManager.js'

export default {
  data() {
    return {
      cartList: [],
      totalPrice: '0.00',
      totalSave: 0,
      totalCount: 0,
      userId: uni.getStorageSync('userId') || 1,
      loading: false,
      editMode: false,
      selectedItems: [],
      recommendList: [
        { id: 1, name: '番茄炒蛋', price: 15.0, coverImg: '/static/dishes/tomato-egg.jpg' },
        { id: 2, name: '宫保鸡丁', price: 28.0, coverImg: '/static/dishes/kungpao-chicken.jpg' },
        { id: 3, name: '麻婆豆腐', price: 12.0, coverImg: '/static/dishes/mapo-tofu.jpg' }
      ]
    }
  },
  computed: {
    isAllSelected() {
      return this.selectedItems.length === this.cartList.length && this.cartList.length > 0
    }
  },
  onShow() {
    this.initData()
  },
  methods: {
    async initData() {
      this.loading = true
      try {
        this.cartList = await getCartList(this.userId)
        this.calcTotal()
      } catch (err) {
        console.error('获取购物车失败：', err)
        uni.showToast({ title: '购物车加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    async updateCount(dishId, count) {
      if (count < 1) {
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

    async deleteItem(dishId) {
      try {
        await deleteCartItem(this.userId, dishId)
        this.initData()
        uni.showToast({ title: '删除成功' })
      } catch (err) {
        console.error('删除失败：', err)
        uni.showToast({ title: '删除失败', icon: 'none' })
      }
    },

    async batchDelete() {
      if (this.selectedItems.length === 0) {
        uni.showToast({ title: '请选择要删除的商品', icon: 'none' })
        return
      }

      uni.showModal({
        title: '确认删除',
        content: `确定要删除选中的${this.selectedItems.length}件商品吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              for (const dishId of this.selectedItems) {
                await deleteCartItem(this.userId, dishId)
              }
              this.selectedItems = []
              this.initData()
              uni.showToast({ title: '删除成功' })
            } catch (err) {
              console.error('批量删除失败：', err)
              uni.showToast({ title: '删除失败', icon: 'none' })
            }
          }
        }
      })
    },

    toggleEditMode() {
      this.editMode = !this.editMode
      if (!this.editMode) {
        this.selectedItems = []
      }
    },

    toggleSelect(item) {
      const index = this.selectedItems.indexOf(item.dishId)
      if (index === -1) {
        this.selectedItems.push(item.dishId)
      } else {
        this.selectedItems.splice(index, 1)
      }
    },

    toggleSelectAll() {
      if (this.isAllSelected) {
        this.selectedItems = []
      } else {
        this.selectedItems = this.cartList.map(item => item.dishId)
      }
    },

    addRecommend(item) {
      // 添加推荐商品到购物车
      uni.showToast({ title: `已添加${item.name}`, icon: 'success' })
      // 这里可以调用添加购物车API
      // await addToCart(this.userId, item.id, 1)
      // this.initData()
    },

    calcTotal() {
      const total = this.cartList.reduce((sum, item) => {
        return sum + (item.price || 0) * item.count
      }, 0)
      this.totalPrice = total.toFixed(2)

      // 计算总数量
      this.totalCount = this.cartList.reduce((sum, item) => sum + item.count, 0)

      // 如果有原价，可以计算节省金额
      // this.totalSave = this.cartList.reduce((sum, item) => {
      //   return sum + (item.originalPrice - item.price) * item.count
      // }, 0)
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
    }
  }
}
</script>

<style scoped>
/* 全局变量 */
.cart-page {
  --primary-color: #ff6700;
  --primary-gradient: linear-gradient(135deg, #ff6700, #ff8c00);
  --secondary-color: #ff9500;
  --background-color: #f8f9fa;
  --card-background: #ffffff;
  --text-primary: #333333;
  --text-secondary: #666666;
  --text-light: #999999;
  --border-color: #eeeeee;
  --shadow-light: 0 2rpx 20rpx rgba(0, 0, 0, 0.06);
  --shadow-medium: 0 8rpx 30rpx rgba(0, 0, 0, 0.1);
  --radius-small: 12rpx;
  --radius-medium: 20rpx;
  --radius-large: 30rpx;
}

/* 导航栏 */
.navbar {
  height: 88rpx;
  background: var(--card-background);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  border-bottom: 1rpx solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-title {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
}

.navbar-edit {
  font-size: 30rpx;
  color: var(--primary-color);
  padding: 8rpx 16rpx;
  border-radius: var(--radius-small);
}

.navbar-edit:active {
  background: rgba(255, 103, 0, 0.1);
}

/* 空状态 */
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 40rpx;
  text-align: center;
}

.empty-image {
  width: 300rpx;
  height: 300rpx;
  margin-bottom: 40rpx;
  opacity: 0.8;
}

.empty-title {
  font-size: 36rpx;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 16rpx;
}

.empty-desc {
  font-size: 28rpx;
  color: var(--text-light);
  margin-bottom: 60rpx;
}

.empty-btn {
  width: 360rpx;
  height: 88rpx;
  background: var(--primary-gradient);
  color: white;
  border-radius: var(--radius-large);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  font-size: 32rpx;
  font-weight: 500;
  border: none;
  box-shadow: var(--shadow-light);
}

.btn-icon {
  font-size: 36rpx;
}

.btn-text {
  font-size: 32rpx;
}

/* 购物车列表 */
.cart-content {
  padding: 32rpx;
  padding-bottom: 160rpx;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.cart-item {
  background: var(--card-background);
  border-radius: var(--radius-medium);
  padding: 32rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
  box-shadow: var(--shadow-light);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.cart-item:active {
  transform: translateY(-4rpx);
  box-shadow: var(--shadow-medium);
}

.item-editing {
  background: rgba(255, 103, 0, 0.02);
  border: 1rpx solid rgba(255, 103, 0, 0.1);
}

/* 选择框 */
.item-select {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.select-icon {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  border: 2rpx solid var(--text-light);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.select-icon.selected {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.icon-check {
  color: white;
  font-size: 24rpx;
  font-weight: bold;
}

/* 商品图片 */
.item-image-wrapper {
  position: relative;
  width: 140rpx;
  height: 140rpx;
  border-radius: var(--radius-small);
  overflow: hidden;
  flex-shrink: 0;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-badge {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  background: var(--primary-color);
  color: white;
  font-size: 20rpx;
  font-weight: 600;
  min-width: 36rpx;
  height: 36rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
  border: 2rpx solid white;
}

/* 商品信息 */
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.item-name {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
}

.item-desc {
  font-size: 26rpx;
  color: var(--text-light);
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8rpx;
}

.item-price {
  font-size: 36rpx;
  font-weight: 700;
  color: var(--primary-color);
}

/* 删除按钮 */
.item-delete {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: rgba(255, 59, 48, 0.1);
  border-radius: var(--radius-small);
  color: #ff3b30;
  font-size: 26rpx;
}

.delete-icon {
  font-size: 28rpx;
}

/* 数量选择器 */
.item-count {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: var(--background-color);
  border-radius: var(--radius-small);
  padding: 8rpx;
}

.count-btn {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: var(--text-primary);
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.count-btn:active {
  transform: scale(0.95);
}

.count-btn.disabled {
  background: #f5f5f5;
  color: #cccccc;
  box-shadow: none;
}

.count-btn.plus {
  background: var(--primary-color);
  color: white;
}

.count-num {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  min-width: 60rpx;
  text-align: center;
}

/* 推荐商品 */
.recommend-section {
  margin-top: 60rpx;
}

.section-title {
  font-size: 34rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 32rpx;
}

.recommend-list {
  display: flex;
  gap: 24rpx;
  overflow-x: auto;
  padding-bottom: 20rpx;
}

.recommend-item {
  width: 200rpx;
  flex-shrink: 0;
  background: var(--card-background);
  border-radius: var(--radius-medium);
  overflow: hidden;
  box-shadow: var(--shadow-light);
  transition: all 0.3s ease;
}

.recommend-item:active {
  transform: translateY(-4rpx);
  box-shadow: var(--shadow-medium);
}

.recommend-img {
  width: 100%;
  height: 160rpx;
  object-fit: cover;
}

.recommend-name {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-primary);
  padding: 20rpx 16rpx 8rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recommend-price {
  font-size: 26rpx;
  color: var(--primary-color);
  font-weight: 600;
  padding: 0 16rpx 20rpx;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--card-background);
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
  z-index: 100;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}

.edit-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.select-text {
  font-size: 30rpx;
  color: var(--text-primary);
}

.delete-btn {
  width: 240rpx;
  height: 80rpx;
  background: #ff3b30;
  color: white;
  border-radius: var(--radius-large);
  font-size: 30rpx;
  font-weight: 500;
  border: none;
}

.delete-btn:disabled {
  background: #cccccc;
  opacity: 0.6;
}

.settle-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.total-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.total-label {
  font-size: 26rpx;
  color: var(--text-light);
}

.total-price {
  font-size: 48rpx;
  font-weight: 700;
  color: var(--primary-color);
  line-height: 1;
}

.total-save {
  font-size: 24rpx;
  color: #ff9500;
  background: rgba(255, 149, 0, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  align-self: flex-start;
}

.settle-btn {
  width: 280rpx;
  height: 88rpx;
  background: var(--primary-gradient);
  color: white;
  border-radius: var(--radius-large);
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  box-shadow: var(--shadow-light);
}

.settle-btn:disabled {
  background: #cccccc;
  opacity: 0.6;
}

/* 加载动画 */
.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
}

.loading-spinner {
  width: 80rpx;
  height: 80rpx;
  border: 6rpx solid #f3f3f3;
  border-top: 6rpx solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: var(--text-light);
}

/* 交互效果 */
.btn-hover {
  opacity: 0.9;
  transform: scale(0.98);
}
</style>