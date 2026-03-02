<template>
  <view class="dish-detail-container" v-if="dish && dish.id">
    <!-- 菜品基础信息 -->
    <view class="dish-base-info">
      <image 
        class="dish-big-img" 
        :src="dish.coverImg || '/static/logo.png'" 
        mode="aspectFill"
        @error="dish.coverImg = ''"
      ></image>
      <view class="dish-base-content">
        <text class="dish-title">{{ dish.dishName || '未知菜品' }}</text>
        <text class="dish-price">¥{{ (dish.price || 0).toFixed(2) }}</text>
        <text class="dish-desc">{{ dish.description || '暂无菜品描述' }}</text>
        
        <!-- 操作按钮区域 -->
        <view class="action-btns">
          <!-- 加入购物车按钮 -->
          <button
            class="add-cart-btn"
            v-if="dish.status === 1 && (dish.stock || 0) > 0"
            @click="addCart"
            hover-class="btn-hover"
          >加入购物车</button>
        </view>
        
        <text class="stock-tips" v-if="dish.status !== 1">
          菜品已下架
        </text>
        <text class="stock-tips" v-else-if="(dish.stock || 0) <= 0">
          库存不足
        </text>
      </view>
    </view>

    <!-- 菜品搭配推荐 -->
    <view class="related-recommend" v-if="filteredRelatedList.length > 0">
      <text class="related-title">搭配推荐</text>
      <scroll-view class="related-scroll" scroll-x="true" show-scrollbar="false">
        <view class="related-list">
          <view 
            class="related-item" 
            v-for="(item, index) in filteredRelatedList" 
            :key="item.id || index"
            @click="goToDishDetail(item.id)"
          >
            <image 
              class="related-img" 
              :src="item.coverImg || '/static/logo.png'" 
              mode="aspectFill"
              @error="item.coverImg = ''"
            ></image>
            <text class="related-name">{{ item.dishName || '未知菜品' }}</text>
            <text class="related-price">¥{{ (item.price || 0).toFixed(2) }}</text>
          </view>
        </view>
      </scroll-view>
    </view>
    
    <!-- 搭配推荐空状态 -->
    <view class="related-empty" v-if="!loading && filteredRelatedList.length === 0">
      <text class="empty-text">暂无搭配推荐</text>
    </view>
  </view>
  
  <!-- 加载中/空状态 -->
  <view class="empty-container" v-else>
    <text class="empty-text">{{ loading ? '菜品详情加载中...' : '暂无菜品信息' }}</text>
  </view>
</template>

<script>
import { getDishDetail } from '../../api/dish.js'
import { getRelatedRecommend } from '@/api/recommend.js'
import { addCart } from '@/utils/cartManager.js'

export default {
  data() {
    return {
      dishId: '',
      dish: null,
      relatedList: [],
      userId: uni.getStorageSync('userId') || 1,
      loading: true,
      jumping: false
    }
  },
  computed: {
    filteredRelatedList() {
      return this.relatedList.filter(item => {
        if (!item) return false
        return item.id != null && item.id !== ''
      })
    }
  },
  onLoad(options) {
    if (!options?.dishId || isNaN(Number(options.dishId))) {
      uni.showToast({ title: '菜品ID不合法', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 1000)
      return
    }
    this.dishId = Number(options.dishId)
    this.getDishDetail()
    this.getRelatedRecommend()
  },
  methods: {
    async getDishDetail() {
      try {
        this.loading = true
        const res = await getDishDetail(this.dishId)
        this.dish = res?.data || res || {}
      } catch (err) {
        console.error('获取菜品详情失败：', err)
        this.dish = null
        uni.showToast({ title: '菜品详情加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async getRelatedRecommend() {
      try {
        if (!this.dishId) return
        const res = await getRelatedRecommend(this.dishId, 3)
        const rawList = res?.data || res || []
        this.relatedList = Array.isArray(rawList) ? rawList : [rawList]
      } catch (err) {
        console.error('获取搭配推荐失败：', err)
        this.relatedList = []
      }
    },
    async addCart() {
      if (!this.dish?.id) {
        uni.showToast({ title: '菜品信息异常', icon: 'none' })
        return
      }
      if (this.dish.status !== 1) {
        uni.showToast({ title: '菜品已下架', icon: 'none' })
        return
      }
      if ((this.dish.stock || 0) <= 0) {
        uni.showToast({ title: '库存不足', icon: 'none' })
        return
      }
      try {
        await addCart(this.userId, this.dish.id, 1)
        uni.showToast({ title: '加入购物车成功', icon: 'success' })
      } catch (err) {
        console.error('加入购物车失败：', err)
        uni.showToast({ title: '加入购物车失败', icon: 'none' })
      }
    },
    goToDishDetail(dishId) {
      if (!dishId || this.jumping) return
      this.jumping = true
      uni.redirectTo({
        url: `/pages/dish/detail?dishId=${dishId}`,
        fail: () => {
          this.jumping = false
          uni.showToast({ title: '跳转失败', icon: 'none' })
        },
        complete: () => setTimeout(() => this.jumping = false, 500)
      })
    }
  }
}
</script>

<style scoped>
/* --- 全局容器 --- */
.dish-detail-container {
  padding-bottom: 40rpx;
  background-color: #f8fafc;
  min-height: 100vh;
}

/* --- 基础信息卡片 --- */
.dish-base-info {
  background: #ffffff;
  padding: 0;
  border-radius: 0 0 24rpx 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
  margin-bottom: 24rpx;
  overflow: hidden;
}

.dish-big-img {
  width: 100%;
  height: 480rpx;
  border-radius: 0;
  background-color: #f1f5f9;
  display: block;
}

.dish-base-content {
  padding: 24rpx 24rpx 30rpx 24rpx;
}

.dish-title {
  font-size: 40rpx;
  color: #1e293b;
  font-weight: 700;
  display: block;
  margin-bottom: 12rpx;
  line-height: 1.3;
}

.dish-price {
  font-size: 42rpx;
  color: #ff5000;
  font-weight: 800;
  display: block;
  margin-bottom: 16rpx;
  font-family: DIN, 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.dish-price::before {
  content: '¥';
  font-size: 28rpx;
  margin-right: 4rpx;
  font-weight: 600;
}

.dish-desc {
  font-size: 28rpx;
  color: #64748b;
  line-height: 1.8;
  display: block;
  margin-bottom: 30rpx;
  background: #f8fafc;
  padding: 16rpx;
  border-radius: 12rpx;
  border: 1rpx solid #e2e8f0;
}

/* --- 操作按钮区域 --- */
.action-btns {
  display: flex;
  gap: 20rpx;
}

.add-cart-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  font-weight: 600;
  background: linear-gradient(135deg, #07c160 0%, #02b454 100%);
  color: #fff;
  border: none;
  border-radius: 44rpx;
  box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.35);
}

.add-cart-btn:active {
  transform: scale(0.96);
  opacity: 1;
}

.btn-hover {
  opacity: 1;
}

/* --- 状态提示 --- */
.stock-tips {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 88rpx;
  font-size: 30rpx;
  color: #94a3b8;
  text-align: center;
  background: #f1f5f9;
  border-radius: 44rpx;
  font-weight: 500;
  border: 1rpx dashed #cbd5e1;
}

/* --- 搭配推荐区域 --- */
.related-recommend {
  background: #ffffff;
  margin: 0 24rpx 24rpx 24rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
  min-height: 240rpx;
}

.related-title {
  font-size: 34rpx;
  color: #1e293b;
  font-weight: 700;
  display: block;
  margin-bottom: 24rpx;
  position: relative;
  padding-left: 16rpx;
}

.related-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 8rpx;
  height: 32rpx;
  background: #07c160;
  border-radius: 4rpx;
}

.related-scroll {
  white-space: nowrap;
  padding-bottom: 10rpx;
}

.related-list {
  display: inline-flex;
  gap: 20rpx;
}

.related-item {
  width: 180rpx;
  text-align: left;
  flex-shrink: 0;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  border: 1rpx solid #f1f5f9;
  transition: transform 0.2s;
}

.related-item:active {
  transform: scale(0.95);
}

.related-img {
  width: 180rpx;
  height: 180rpx;
  border-radius: 16rpx 16rpx 0 0;
  background-color: #f1f5f9;
  display: block;
}

.related-name {
  font-size: 26rpx;
  color: #334155;
  font-weight: 600;
  display: block;
  margin: 12rpx 12rpx 8rpx 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-price {
  font-size: 28rpx;
  color: #ff5000;
  font-weight: 700;
  display: block;
  margin: 0 12rpx 12rpx 12rpx;
  font-family: DIN, 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.related-price::before {
  content: '¥';
  font-size: 20rpx;
  margin-right: 2rpx;
}

/* --- 空状态 --- */
.related-empty {
  background: #ffffff;
  margin: 0 24rpx 24rpx 24rpx;
  padding: 60rpx 24rpx;
  text-align: center;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8fafc;
}

.empty-text {
  font-size: 30rpx;
  color: #94a3b8;
  font-weight: 500;
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.dish-base-info, .related-recommend {
  animation: fadeIn 0.4s ease-out;
}
</style>