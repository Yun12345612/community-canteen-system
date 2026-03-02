<template>
  <view class="index-container">
    <view class="banner-card">
      <text class="banner-main">社区食堂点餐系统</text>
      <text class="banner-sub">新鲜食材 · 健康膳食 · 便捷服务</text>
    </view>

    <view class="func-grid">
      <view class="func-item" hover-class="item-hover" @click="goToDishList">
        <text class="func-icon">🍽️</text>
        <text class="func-text">在线点餐</text>
      </view>
      <view class="func-item" hover-class="item-hover" @click="goToOrderList">
        <text class="func-icon">📋</text>
        <text class="func-text">我的订单</text>
      </view>
    </view>

    <view class="notice-card">
      <view class="notice-header">
        <text class="notice-title">平台公告</text>
      </view>
      <view class="notice-list">
        <view class="notice-item">• 每日营业时间：07:00 - 20:00</view>
        <view class="notice-item">• 支持堂食、打包外带两种就餐方式</view>
        <view class="notice-item">• 微信支付秒结算，订单状态实时更新</view>
        <view class="notice-item">• 食堂提供免费餐具，环保从我做起</view>
      </view>
    </view>

    <!-- 猜你喜欢推荐模块 -->
    <view class="recommend-card" v-if="filteredRecommendList.length > 0">
      <view class="recommend-header">
        <text class="recommend-title">猜你喜欢</text>
        <text class="recommend-more" @click="goToDishList">更多</text>
      </view>
      <scroll-view class="recommend-scroll" scroll-x="true" show-scrollbar="false">
        <view class="recommend-list">
          <view 
            class="recommend-item" 
            v-for="(dish, index) in filteredRecommendList" 
            :key="dish.id || index"
            @click="goToDishDetail(dish.id)"
          >
            <image 
              class="recommend-img" 
              :src="dish.coverImg || '/static/logo.png'" 
              mode="aspectFill"
              lazy-load
              @error="dish.coverImg = ''"
            ></image>
            <text class="recommend-name">{{ dish.dishName || '未知菜品' }}</text>
            <text class="recommend-price">¥{{ (dish.price || 0).toFixed(2) }}</text>
          </view>
        </view>
      </scroll-view>
    </view>
    
    <!-- 推荐空状态 -->
    <view class="recommend-empty" v-if="!loading && filteredRecommendList.length === 0">
      <text class="empty-text">暂无推荐菜品</text>
    </view>

    <!-- 底部自定义导航组件，current-index=0 表示首页选中 -->
    <custom-tabbar :current-index="0"></custom-tabbar>
  </view>
</template>

<script>
import { getUserRecommend, getHybridRecommend } from '@/api/recommend.js'
//引入自定义tabBar组件
import customTabbar from "@/components/custom-tabbar/custom-tabbar.vue"

export default {
  // 注册组件
  components: {
    customTabbar
  },
  data() {
    return {
      recommendList: [],
      userId: uni.getStorageSync('userId') || 1,
      loading: true
    }
  },
  computed: {
    filteredRecommendList() {
      return this.recommendList.filter(dish => {
        if (!dish) return false
        return typeof dish.id === 'number' && dish.id > 0
      })
    }
  },
  onLoad() {
    this.fetchHybridRecommend()
  },
  methods: {
    goToDishList() {
       uni.redirectTo({ url: "/pages/dish/list" })
    },
    goToOrderList() {
      uni.redirectTo({ url: "/pages/order/list" })
    },
    goToDishDetail(dishId) {
      if (!dishId) return
      uni.navigateTo({ url: `/pages/dish/detail?dishId=${dishId}` })
    },
    async fetchUserRecommend() {
      try {
        this.loading = true
        const res = await getUserRecommend(this.userId, 5)
        const rawList = res?.data || res || []
        this.recommendList = Array.isArray(rawList) ? rawList : []
      } catch (err) {
        console.error('获取猜你喜欢失败：', err)
        this.recommendList = []
      } finally {
        this.loading = false
      }
    },
    async fetchHybridRecommend() {
      try {
        this.loading = true
        const res = await getHybridRecommend(this.userId, 5)
        const rawList = res?.data || res || []
        this.recommendList = Array.isArray(rawList) ? rawList : []
      } catch (err) {
        console.error('获取融合推荐失败：', err)
        this.recommendList = []
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.index-container {
  padding: 20rpx;
  background-color: #f7f8fa;
  min-height: 100vh;
  box-sizing: border-box;
  /* 底部留50px空间给导航栏，避免内容被遮挡 */
  padding-bottom: 50px;
}

.banner-card {
  background: linear-gradient(135deg, #07c160, #02a850);
  border-radius: 24rpx;
  padding: 50rpx 32rpx;
  color: #fff;
  margin-bottom: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.15);
}

.banner-main {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 12rpx;
}

.banner-sub {
  font-size: 26rpx;
  opacity: 0.9;
}

.func-grid {
  display: flex;
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.func-item {
  flex: 1;
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 20rpx;
  text-align: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.item-hover {
  transform: scale(0.98);
  opacity: 0.9;
}

.func-icon {
  font-size: 56rpx;
  display: block;
  margin-bottom: 16rpx;
}

.func-text {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.notice-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 32rpx;
}

.notice-header {
  margin-bottom: 20rpx;
}

.notice-title {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.notice-item {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
}

.recommend-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.recommend-title {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.recommend-more {
  font-size: 24rpx;
  color: #ff6700;
}

.recommend-scroll {
  white-space: nowrap;
}

.recommend-list {
  display: flex;
  gap: 20rpx;
  padding-bottom: 10rpx;
  min-height: 220rpx;
}

.recommend-item {
  width: 180rpx;
  text-align: center;
  flex-shrink: 0;
}

.recommend-img {
  width: 180rpx;
  height: 180rpx;
  border-radius: 12rpx;
  background-color: #f2f2f2;
  margin-bottom: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.recommend-name {
  font-size: 26rpx;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-price {
  font-size: 28rpx;
  color: #ff6700;
  font-weight: bold;
}

.recommend-empty {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  text-align: center;
  margin-bottom: 32rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
