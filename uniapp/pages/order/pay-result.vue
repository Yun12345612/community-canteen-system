<template>
  <view class="result-container">
    <view class="status-card">
      <text v-if="status === 'success'" class="success-icon">✅</text>
      <text v-else class="fail-icon">❌</text>
      <view class="status-title">{{ status === 'success' ? '支付成功' : '支付失败' }}</view>
      <view class="status-desc">{{ status === 'success' ? '商家已收到订单，开始准备餐品' : '支付未完成，可返回重新下单' }}</view>
    </view>
    <button class="back-btn" @click="backHome" hover-class="btn-hover">返回点餐</button>
  </view>
</template>

<script>
export default {
  data() {
    return {
      status: 'success'
    }
  },
  onLoad(options) {
    this.status = options.status || 'fail'
  },
  methods: {
    backHome() {
      uni.redirectTo({ url: '/pages/dish/list' })
    }
  }
}
</script>

<style scoped>
.result-container {
  padding: 120rpx 40rpx;
  min-height: 100vh;
  background: #f7f8fa;
  box-sizing: border-box;
  text-align: center;
}

.status-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  margin-bottom: 80rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.06);
}

.success-icon, .fail-icon {
  font-size: 120rpx;
  display: block;
  margin-bottom: 24rpx;
}

.status-title {
  font-size: 42rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.status-desc {
  font-size: 28rpx;
  color: #999;
  line-height: 1.5;
}

.back-btn {
  width: 320rpx;
  height: 80rpx;
  background: #ff6700;
  color: #fff;
  border: none;
  border-radius: 40rpx;
  font-size: 30rpx;
}

.btn-hover {
  opacity: 0.9;
  transform: scale(0.98);
}
</style>