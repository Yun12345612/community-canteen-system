<template>
  <view class="mine-container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="avatar-wrapper" @click="chooseAvatar">
        <image :src="userInfo.avatar || '/static/default-avatar.png'" class="avatar" mode="aspectFill"></image>
        <view class="edit-tag">更换</view>
      </view>
      <view class="user-info">
        <view class="nickname">{{ userInfo.nickname || '未设置昵称' }}</view>
        <view class="signature" @click="editSignature">
          {{ userInfo.signature || '点击设置个性签名' }}
          <text class="pen-icon">✏️</text>
        </view>
      </view>
    </view>

    <!-- 菜单列表 -->
    <view class="menu-wrapper">
      <view class="menu-group">
        <view class="menu-item" hover-class="item-active" @click="editNickname">
          <view class="menu-left">
            <text class="menu-icon">👤</text>
            <text class="menu-title">修改昵称</text>
          </view>
          <view class="menu-right">
            <text class="menu-value">{{ userInfo.nickname || '未设置' }}</text>
            <text class="arrow">></text>
          </view>
        </view>
        <view class="menu-item divider" hover-class="item-active" @click="toOrderList">
          <view class="menu-left">
            <text class="menu-icon">📋</text>
            <text class="menu-title">我的订单</text>
          </view>
          <view class="menu-right">
            <text class="arrow">></text>
          </view>
        </view>
        <view class="menu-item divider" hover-class="item-active" @click="toEvaluationList">
          <view class="menu-left">
            <text class="menu-icon">📝</text>
            <text class="menu-title">我的评价</text>
          </view>
          <view class="menu-right">
            <text class="arrow">></text>
          </view>
        </view>
        <view class="menu-item" hover-class="item-active" @click="toAbout">
          <view class="menu-left">
            <text class="menu-icon">ℹ️</text>
            <text class="menu-title">关于我们</text>
          </view>
          <view class="menu-right">
            <text class="arrow">></text>
          </view>
        </view>
      </view>
    </view>

    <!-- 退出登录按钮 -->
    <view class="logout-btn" hover-class="logout-active" @click="logout">
      <text class="logout-text">退出登录</text>
    </view>

    <!-- 修改昵称弹窗 -->
    <uni-popup ref="nicknamePopup" type="dialog">
      <view class="popup-content">
        <view class="popup-title">修改昵称</view>
        <input class="popup-input" v-model="editForm.nickname" placeholder="请输入新昵称" maxlength="10" />
        <view class="popup-btns">
          <button class="popup-btn cancel" @click="closeNicknamePopup">取消</button>
          <button class="popup-btn confirm" @click="saveNickname">确定</button>
        </view>
      </view>
    </uni-popup>

    <!-- 修改签名弹窗 -->
    <uni-popup ref="signaturePopup" type="dialog">
      <view class="popup-content">
        <view class="popup-title">修改个性签名</view>
        <textarea class="popup-textarea" v-model="editForm.signature" placeholder="请输入个性签名" maxlength="30"></textarea>
        <view class="popup-btns">
          <button class="popup-btn cancel" @click="closeSignaturePopup">取消</button>
          <button class="popup-btn confirm" @click="saveSignature">确定</button>
        </view>
      </view>
    </uni-popup>

    <!-- 底部自定义导航栏 -->
    <custom-tabbar :current-index="3"></custom-tabbar>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import uniPopup from '@dcloudio/uni-ui/lib/uni-popup/uni-popup.vue'
import { onShow } from '@dcloudio/uni-app'
import { updateUserInfo } from '../../api/user.js'
import { uploadFile } from '../../api/common.js'
import customTabbar from "@/components/custom-tabbar/custom-tabbar.vue"

const userInfo = ref({ id: '', avatar: '', nickname: '', signature: '' })
const editForm = ref({ nickname: '', signature: '' })
const nicknamePopup = ref(null)
const signaturePopup = ref(null)

onShow(() => { loadUserInfo() })

const loadUserInfo = () => {
  const cacheUser = uni.getStorageSync('userInfo')
  if (cacheUser) userInfo.value = { ...userInfo.value, ...cacheUser }
}

const chooseAvatar = async () => {
  uni.chooseImage({
    count: 1, sizeType: ['compressed'], sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempPath = res.tempFilePaths[0]
      uni.showLoading({ title: '上传中...' })
      try {
        const uploadRes = await uploadFile(tempPath)
        userInfo.value.avatar = uploadRes.data
        uni.setStorageSync('userInfo', userInfo.value)
        uni.showToast({ title: '头像更新成功', icon: 'success' })
      } catch (err) {
        uni.showToast({ title: '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    }
  })
}

const editNickname = () => {
  editForm.value.nickname = userInfo.value.nickname || ''
  nicknamePopup.value.open()
}
const closeNicknamePopup = () => nicknamePopup.value.close()
const saveNickname = async () => {
  const newName = editForm.value.nickname.trim()
  if (!newName) return uni.showToast({ title: '昵称不能为空', icon: 'none' })
  try {
    await updateUserInfo({ id: userInfo.value.id, nickname: newName })
    userInfo.value.nickname = newName
    uni.setStorageSync('userInfo', userInfo.value)
    closeNicknamePopup()
    uni.showToast({ title: '修改成功', icon: 'success' })
  } catch (err) {
    uni.showToast({ title: '修改失败', icon: 'none' })
  }
}

const editSignature = () => {
  editForm.value.signature = userInfo.value.signature || ''
  signaturePopup.value.open()
}
const closeSignaturePopup = () => signaturePopup.value.close()
const saveSignature = async () => {
  try {
    await updateUserInfo({ id: userInfo.value.id, signature: editForm.value.signature })
    userInfo.value.signature = editForm.value.signature
    uni.setStorageSync('userInfo', userInfo.value)
    closeSignaturePopup()
    uni.showToast({ title: '修改成功', icon: 'success' })
  } catch (err) {
    uni.showToast({ title: '修改失败', icon: 'none' })
  }
}

const toOrderList = () => {
  uni.navigateTo({
    url: '/pages/order/list'
  })
}

const toEvaluationList = () => {
  console.log('点击了我的评价，准备跳转...')
  uni.navigateTo({
    url: '/pages/evaluation/list',
    success: () => {
      console.log('跳转成功')
    },
    fail: (err) => {
      console.error('跳转失败:', err)
      uni.showToast({ title: '跳转失败: ' + JSON.stringify(err), icon: 'none' })
    }
  })
}

const toAbout = () => uni.showToast({ title: '功能开发中', icon: 'none' })

const logout = () => {
  uni.showModal({
    title: '提示', content: '确定退出登录？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('userInfo')
        uni.removeStorageSync('token')
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}
</script>

<style scoped>
/* --- 全局容器 --- */
.mine-container {
  min-height: 100vh;
  background-color: #f8fafc; /* 统一浅灰白背景 */
  padding-bottom: 60rpx;
  box-sizing: border-box;
}

/* --- 用户卡片：调整为登录页同款绿色渐变 --- */
.user-card {
  /* 使用登录页同款绿色渐变 #07c160 -> #02b454 */
  background: linear-gradient(135deg, #07c160 0%, #02b454 100%);
  padding: 90rpx 40rpx 50rpx;
  border-radius: 0 0 40rpx 40rpx;
  display: flex;
  align-items: center;
  /* 阴影颜色同步 */
  box-shadow: 0 12rpx 32rpx rgba(7, 193, 96, 0.25);
  margin-bottom: 40rpx;
  position: relative;
  overflow: hidden;
}

/* 增加一点装饰性光晕 */
.user-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  pointer-events: none;
}

.avatar-wrapper {
  position: relative;
  margin-right: 40rpx;
  z-index: 2;
}

.avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  border: 6rpx solid rgba(255, 255, 255, 0.5);
  object-fit: cover;
  background: #fff;
  box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.1);
}

.edit-tag {
  position: absolute;
  bottom: 4rpx;
  right: -4rpx;
  background-color: #ffffff;
  /* 文字颜色使用登录页同款绿色 */
  color: #07c160;
  font-size: 22rpx;
  padding: 6rpx 14rpx;
  border-radius: 20rpx;
  font-weight: 700;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.15);
  white-space: nowrap;
}

.user-info {
  flex: 1;
  color: #fff;
  z-index: 2;
}

.nickname {
  font-size: 42rpx;
  font-weight: 700;
  margin-bottom: 16rpx;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1);
}

.signature {
  font-size: 28rpx;
  opacity: 0.95;
  display: flex;
  align-items: center;
  gap: 12rpx;
  max-width: 90%;
}

.pen-icon {
  font-size: 24rpx;
  opacity: 0.8;
}

/* --- 菜单列表 --- */
.menu-wrapper {
  padding: 0 30rpx;
}

.menu-group {
  background-color: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
  border: 1rpx solid rgba(255,255,255,0.6);
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 36rpx 32rpx;
  transition: background-color 0.2s;
}

.item-active {
  background-color: #f1f5f9;
}

.divider {
  border-bottom: 1rpx solid #f1f5f9;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.menu-icon {
  font-size: 36rpx;
  width: 48rpx;
  text-align: center;
}

.menu-title {
  font-size: 32rpx;
  color: #1e293b;
  font-weight: 600;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.menu-value {
  font-size: 28rpx;
  color: #94a3b8;
  max-width: 200rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  font-size: 32rpx;
  color: #cbd5e1;
  font-weight: bold;
}

/* --- 退出登录按钮：保持红色警示色 --- */
.logout-btn {
  margin: 80rpx 40rpx 0;
  height: 88rpx;
  background-color: #fff;
  border: 2rpx solid #ef4444;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(239, 68, 68, 0.1);
}

.logout-active {
  transform: scale(0.98);
  background-color: #fef2f2;
}

.logout-text {
  color: #ef4444;
  font-size: 32rpx;
  font-weight: 600;
}

/* --- 弹窗样式 --- */
.popup-content {
  width: 620rpx;
  padding: 50rpx 40rpx;
  background-color: #fff;
  border-radius: 30rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.12);
  text-align: center;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 40rpx;
}

.popup-input, .popup-textarea {
  width: 100%;
  padding: 24rpx;
  border: 2rpx solid #e2e8f0;
  border-radius: 16rpx;
  font-size: 30rpx;
  box-sizing: border-box;
  margin-bottom: 50rpx;
  background: #f8fafc;
  color: #333;
}

.popup-input:focus, .popup-textarea:focus {
  /* 聚焦边框颜色使用登录页同款绿色 */
  border-color: #07c160;
  background: #fff;
}

.popup-textarea {
  min-height: 160rpx;
  resize: none;
}

.popup-btns {
  display: flex;
  gap: 32rpx;
}

.popup-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: 600;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel {
  background-color: #f1f5f9;
  color: #64748b;
}

/* 登录页同款绿色渐变 */
.confirm {
  /* 使用登录页同款绿色渐变 */
  background: linear-gradient(135deg, #07c160 0%, #02b454 100%);
  color: #fff;
  /* 阴影颜色同步 */
  box-shadow: 0 6rpx 16rpx rgba(7, 193, 96, 0.3);
}

.confirm:active {
  transform: scale(0.96);
}
</style>
