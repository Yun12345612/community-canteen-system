<template>
  <view class="login-wrapper">
    <view class="brand-section">
      <view class="logo-box">
        <text class="logo-text">幸福里</text>
      </view>
      <text class="title-text">社区食堂系统</text>
      <text class="subtitle-text">便捷点餐 · 安心用餐</text>
    </view>

    <view class="login-section">
      <button class="login-btn" @click="handleWechatLogin" hover-class="btn-hover">微信一键登录</button>
      <text class="tip-text">未注册账号将自动创建</text>
    </view>

    <view class="agreement-section">
      <text class="tip">登录即代表同意</text>
      <text class="link">《用户服务协议》</text>
      <text class="link">《隐私政策》</text>
    </view>
  </view>
</template>

<script setup>
import { wechatLogin, h5Login } from '../../api/user.js'

const handleWechatLogin = async () => {
  try {
    // #ifdef H5
    const storageKey = 'h5DeviceId'
    let deviceId = uni.getStorageSync(storageKey)
    if (!deviceId) {
      deviceId = `${Date.now()}_${Math.random().toString(16).slice(2)}`
      uni.setStorageSync(storageKey, deviceId)
    }
    const res = await h5Login(deviceId)
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('userInfo', res.data.userInfo)
    uni.setStorageSync('userId', res.data.userInfo.id || res.data.userId)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 500)
    return
    // #endif

    // #ifndef H5
    const loginRes = await uni.login({ provider: 'weixin' })
    const code = loginRes.code
    if (!code) {
      uni.showToast({ title: '获取凭证失败', icon: 'none' })
      return
    }
    const res = await wechatLogin(code)
    // ==========补充存储 userId ==========
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('userInfo', res.data.userInfo)
    // 从 userInfo 中提取 userId 并单独存储（关键！）
    uni.setStorageSync('userId', res.data.userInfo.id || res.data.userId)
    // ==============================================
    
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
    uni.reLaunch({ url: '/pages/index/index' })
    }, 1500)
    // #endif
  } catch (error) {
    console.error('登录异常：', error)
    uni.showToast({ title: '登录失败，请重试', icon: 'none' })
  }
}
</script>

<style scoped>
/* 样式保持不变 */
.login-wrapper {
  min-height: 100vh;
  padding: 120rpx 40rpx 60rpx;
  box-sizing: border-box;
  background: linear-gradient(135deg, #f8fbf8 0%, #f1f8f4 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
}

.brand-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 120rpx;
}

.logo-box {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #07c160, #02b454);
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40rpx;
  box-shadow: 0 12rpx 24rpx rgba(7, 193, 96, 0.15);
}

.logo-text {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
}

.title-text {
  font-size: 48rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 16rpx;
}

.subtitle-text {
  font-size: 28rpx;
  color: #888;
}

.login-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-btn {
  width: 100%;
  max-width: 600rpx;
  height: 88rpx;
  line-height: 88rpx;
  background: #07c160;
  color: #fff;
  border-radius: 44rpx;
  border: none;
  font-size: 32rpx;
  font-weight: 500;
  box-shadow: 0 8rpx 20rpx rgba(7, 193, 96, 0.2);
  margin-bottom: 24rpx;
}

.btn-hover {
  transform: scale(0.98);
  opacity: 0.9;
}

.tip-text {
  font-size: 24rpx;
  color: #999;
}

.agreement-section {
  width: 100%;
  max-width: 600rpx;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
  color: #bbb;
  margin-top: 40rpx;
}

.link {
  color: #07c160;
  margin: 0 8rpx;
}
</style>
