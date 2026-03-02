<template>
  <view class="tabbar-wrapper">
    <view class="tabbar">
      <!-- 首页 -->
      <view class="tab-item" :class="{active: currentIndex === 0}" @click="jump(0)">
        <text class="icon">🏠</text>
        <text class="text">首页</text>
      </view>
      <!-- 点餐 -->
      <view class="tab-item" :class="{active: currentIndex === 1}" @click="jump(1)">
        <text class="icon">🍽️</text>
        <text class="text">点餐</text>
      </view>
      <!-- 订单 -->
      <view class="tab-item" :class="{active: currentIndex === 2}" @click="jump(2)">
        <text class="icon">📋</text>
        <text class="text">订单</text>
      </view>
      <!-- 我的 -->
      <view class="tab-item" :class="{active: currentIndex === 3}" @click="jump(3)">
        <text class="icon">👤</text>
        <text class="text">我的</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: "custom-tabbar",
  props: {
    currentIndex: {
      type: Number,
      default: 0
    }
  },
  methods: {
    jump(index) {
      const paths = [
        "/pages/index/index",
        "/pages/dish/list",
        "/pages/order/list",
        "/pages/mine/mine"
      ];
      uni.reLaunch({ url: paths[index] });
    }
  }
};
</script>

<style scoped>
/* 固定底部，优先级拉满 */
.tabbar-wrapper {
  position: fixed !important;
  bottom: 0 !important;
  left: 0 !important;
  right: 0 !important;
  z-index: 9999 !important;
  height: 50px !important;
  background: #fff !important;
  border-top: 1px solid #eee !important;
}

/* 导航栏容器 */
.tabbar {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: space-around;
}

/* 单个导航项 */
.tab-item {
  flex: 1;
  text-align: center;
}

/* 图标 */
.icon {
  font-size: 36rpx;
  display: block;
  margin-top: 8rpx;
  color: #666;
}

/* 文字 */
.text {
  font-size: 24rpx;
  color: #666;
  margin-top: 4rpx;
}

/* 选中状态（和uni-tabbar的active-color一致） */
.tab-item.active .icon,
.tab-item.active .text {
  color: #ff6700 !important;
}
</style>