<template>
  <view class="evaluation-container">
    <!-- 页面标题 -->
    <view class="page-header">
      <text class="title">评价订单</text>
    </view>

    <!-- 加载中 -->
    <view class="loading-box" v-if="loading">
      <text class="loading-text">加载订单信息中...</text>
    </view>

    <!-- 评价表单 -->
    <view class="evaluation-form" v-else>
      <!-- 订单评分 -->
      <view class="order-score-section">
        <text class="section-label">订单评分</text>
        <view class="score-group">
          <text
            class="star"
            :class="{ active: starIdx < orderScore }"
            v-for="(star, starIdx) in 5"
            :key="starIdx"
            @click="setOrderScore(starIdx + 1)"
          >★</text>
          <text class="score-text">{{ orderScore }}分</text>
        </view>
      </view>

      <!-- 整体评价 -->
      <view class="total-evaluation">
        <text class="total-label">评价内容</text>
        <textarea
          class="total-content"
          v-model="totalContent"
          placeholder="分享你的用餐体验吧~"
        ></textarea>
      </view>

      <!-- 图片上传 -->
      <view class="upload-area">
        <text class="upload-label">上传图片（可选，最多3张）</text>
        <view class="img-list">
          <!-- 已选图片 -->
          <view class="img-item" v-for="(img, index) in imgUrls" :key="index">
            <image :src="img" mode="aspectFill"></image>
            <text class="del-img" @click="deleteImg(index)">×</text>
          </view>
          <!-- 选择图片按钮 -->
          <view class="add-img" @click="chooseImage" v-if="imgUrls.length < 3">
            <text class="add-icon">+</text>
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <button class="submit-btn" @click="submitEvaluation" :disabled="submitting">
        {{ submitting ? '提交中...' : '提交评价' }}
      </button>
    </view>
  </view>
</template>

<script>
import { submitEvaluation } from '../../api/evaluation.js'

export default {
  data() {
    return {
      orderId: '',
      userId: uni.getStorageSync('userId') || '',
      loading: false,
      submitting: false,
      orderScore: 5,
      totalContent: '',
      imgUrls: []
    }
  },
  onLoad(options) {
    setTimeout(async () => {
      this.orderId = options.orderId || ''

      if (!this.userId) {
        uni.showToast({ title: '请先登录后再评价', icon: 'none', duration: 2000 })
        setTimeout(() => this.safeNavigateBack(), 500)
        return
      }

      if (!this.orderId) {
        uni.showToast({ title: '订单ID不能为空', icon: 'none', duration: 2000 })
        setTimeout(() => this.safeNavigateBack(), 500)
        return
      }

      this.loading = false
    }, 100)
  },
  methods: {
    safeNavigateBack() {
      try {
        const pages = getCurrentPages()
        if (pages.length > 1) {
          uni.navigateBack({ delta: 1, fail: () => uni.redirectTo({ url: '/pages/index/index' }) })
        } else {
          uni.redirectTo({ url: '/pages/index/index' })
        }
      } catch (err) {
        uni.redirectTo({ url: '/pages/index/index' })
      }
    },

    setOrderScore(score) {
      this.orderScore = score
    },

    chooseImage() {
      const remainCount = 3 - this.imgUrls.length
      if (remainCount <= 0) {
        uni.showToast({ title: '最多只能上传3张图片', icon: 'none' })
        return
      }

      uni.chooseImage({
        count: remainCount,
        sizeType: ['original', 'compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.uploadImages(res.tempFilePaths)
        },
        fail: () => {
          uni.showToast({ title: '选择图片失败', icon: 'none' })
        }
      })
    },

    async uploadImages(tempFilePaths) {
      uni.hideLoading()
      uni.showLoading({ title: '图片上传中...', mask: true })

      try {
        for (const tempPath of tempFilePaths) {
          const uploadRes = await new Promise((resolve, reject) => {
            uni.uploadFile({
              url: 'http://127.0.0.1:8080/common/upload',
              filePath: tempPath,
              name: 'file',
              success: (res) => {
                try {
                  if (res.statusCode !== 200) { reject(new Error('服务器响应异常')); return }
                  const resData = JSON.parse(res.data)
                  if (resData.code === 200) resolve(resData.data)
                  else reject(new Error(resData.msg || '图片上传失败'))
                } catch (e) { reject(new Error('解析响应失败')) }
              },
              fail: (err) => reject(new Error(err.errMsg || '网络异常'))
            })
          })
          this.imgUrls.push(uploadRes)
        }
        uni.showToast({ title: `上传成功${tempFilePaths.length}张`, icon: 'success' })
      } catch (err) {
        uni.showToast({ title: err.message || '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },

    deleteImg(index) {
      uni.showModal({
        title: '提示',
        content: '确定要删除这张图片吗？',
        success: (res) => {
          if (res.confirm) {
            this.imgUrls.splice(index, 1)
            uni.showToast({ title: '删除成功', icon: 'success', duration: 1000 })
          }
        }
      })
    },

    async submitEvaluation() {
      // 验证
      if (!this.totalContent && this.imgUrls.length === 0) {
        uni.showToast({ title: '请填写评价内容或上传图片', icon: 'none' })
        return
      }

      this.submitting = true
      try {
        const params = {
          orderId: this.orderId,
          userId: Number(this.userId),
          score: this.orderScore,
          content: this.totalContent,
          imgUrls: this.imgUrls.length > 0 ? this.imgUrls : null
        }

        await submitEvaluation(params)
        uni.showToast({ title: '评价成功！', icon: 'success', duration: 2000 })
        setTimeout(() => this.safeNavigateBack(), 1500)
      } catch (err) {
        uni.showToast({ title: err.msg || '评价提交失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
/* --- 全局容器 --- */
.evaluation-container {
  padding: 24rpx;
  min-height: 100vh;
  background: #f8fafc;
  box-sizing: border-box;
}

.page-header {
  padding: 20rpx 0 30rpx;
  margin-bottom: 10rpx;
}
.title {
  font-size: 36rpx;
  font-weight: 800;
  color: #1e293b;
}

.loading-box {
  text-align: center;
  padding: 100rpx 0;
}
.loading-text {
  font-size: 28rpx;
  color: #94a3b8;
}

/* --- 订单评分 --- */
.order-score-section {
  background: #fff;
  padding: 24rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.03);
  margin-bottom: 24rpx;
}

.section-label {
  font-size: 28rpx;
  color: #1e293b;
  font-weight: 600;
  display: block;
  margin-bottom: 16rpx;
}

.score-group {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.star {
  font-size: 48rpx;
  color: #e2e8f0;
  transition: color 0.2s;
  cursor: pointer;
}

.star.active {
  color: #10b981;
  text-shadow: 0 2rpx 4rpx rgba(16, 185, 129, 0.3);
}

.score-text {
  font-size: 28rpx;
  color: #10b981;
  font-weight: 700;
  margin-left: 16rpx;
}

/* --- 整体评价 --- */
.total-evaluation {
  background: #fff;
  padding: 24rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.03);
  margin-bottom: 24rpx;
}

.total-label {
  font-size: 28rpx;
  color: #1e293b;
  display: block;
  margin-bottom: 16rpx;
  font-weight: 600;
}

.total-content {
  width: 100%;
  min-height: 200rpx;
  padding: 16rpx;
  background: #f8fafc;
  border: 1rpx solid #e2e8f0;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #334155;
  box-sizing: border-box;
  line-height: 1.6;
}

.total-content:focus {
  background: #fff;
  border-color: #10b981;
}

/* --- 图片上传区 --- */
.upload-area {
  background: #fff;
  padding: 24rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.03);
  margin-bottom: 40rpx;
}

.upload-label {
  font-size: 28rpx;
  color: #1e293b;
  display: block;
  margin-bottom: 16rpx;
  font-weight: 600;
}

.img-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.img-item {
  width: 160rpx;
  height: 160rpx;
  position: relative;
  border-radius: 12rpx;
  overflow: hidden;
  border: 1rpx solid #e2e8f0;
}

.img-item image {
  width: 100%;
  height: 100%;
}

.del-img {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 36rpx;
  height: 36rpx;
  line-height: 34rpx;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  color: #ef4444;
  border: 1rpx solid #fee2e2;
  border-radius: 50%;
  font-size: 24rpx;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 2rpx 6rpx rgba(0,0,0,0.1);
}

.add-img {
  width: 160rpx;
  height: 160rpx;
  border: 2rpx dashed #a7f3d0;
  background: #ecfdf5;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-img:active {
  background: #d1fae5;
}

.add-icon {
  font-size: 56rpx;
  color: #10b981;
  font-weight: 300;
}

/* --- 提交按钮 --- */
.submit-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: #fff;
  border: none;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: 700;
  box-shadow: 0 8rpx 20rpx rgba(16, 185, 129, 0.35);
  transition: transform 0.2s;
}

.submit-btn:active {
  transform: scale(0.96);
}

.submit-btn:disabled {
  background: #cbd5e1;
  color: #64748b;
  box-shadow: none;
  transform: none;
}
</style>
