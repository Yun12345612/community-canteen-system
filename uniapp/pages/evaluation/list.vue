<template>
  <view class="page">
    <view class="header">
      <text class="title">我的评价</text>
    </view>

    <view v-if="loading" class="loading-box">
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else>
      <view v-if="list.length === 0" class="empty-box">
        <text class="empty-icon">📝</text>
        <text class="empty-text">暂无评价记录</text>
      </view>

      <view v-else class="list">
        <view
          class="card"
          v-for="item in list"
          :key="item.id"
          :class="{ highlight: orderId && item.orderId === orderId }"
        >
          <view class="row">
            <text class="label">订单号</text>
            <text class="value">{{ item.orderNo || '--' }}</text>
          </view>
          <view class="row">
            <text class="label">评分</text>
            <view class="stars">
              <text
                class="star"
                v-for="n in 5"
                :key="n"
                :class="{ active: n <= (item.score || 0) }"
              >★</text>
              <text class="score-text">{{ item.score || 0 }}分</text>
            </view>
          </view>
          <view class="row block">
            <text class="label">评价内容</text>
            <text class="content">{{ item.content || '未填写' }}</text>
          </view>

          <view v-if="parseImageUrls(item.imgUrls).length" class="imgs">
            <image
              v-for="(img, idx) in parseImageUrls(item.imgUrls)"
              :key="idx"
              class="img"
              :src="img"
              mode="aspectFill"
            />
          </view>

          <view class="divider"></view>

          <!-- 菜品评价区域 - 优化显示 -->
          <view v-if="item.dishEvaluations && item.dishEvaluations.length" class="dish-section">
            <view class="dish-title">
              <text class="dish-title-icon">🍽️</text>
              <text class="dish-title-text">菜品评价（{{ item.dishEvaluations.length }}个）</text>
            </view>
            <view class="dish-item" v-for="d in item.dishEvaluations" :key="d.id || d.dishId">
              <view class="dish-head">
                <text class="dish-name">{{ d.dishName || '菜品' }}</text>
                <view class="dish-stars">
                  <text
                    class="dish-star"
                    v-for="n in 5"
                    :key="n"
                    :class="{ active: n <= (d.score || 0) }"
                  >★</text>
                  <text class="dish-score">{{ d.score || 0 }}分</text>
                </view>
              </view>
              <text class="dish-content">{{ d.content || '未填写评价' }}</text>
            </view>
          </view>

          <view class="divider"></view>

          <view class="row block">
            <text class="label">商家回复</text>
            <text class="reply" :class="{ empty: !item.merchantReply }">
              {{ item.merchantReply || '商家暂未回复' }}
            </text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserEvaluations } from '../../api/evaluation.js'

export default {
  data() {
    return {
      list: [],
      loading: false,
      orderId: ''
    }
  },
  onLoad(options) {
    this.orderId = options.orderId || ''
  },
  onShow() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await getUserEvaluations()
        console.log('【评价列表】接口返回数据:', res)
        this.list = (res.data || []).map((x) => {
          console.log('【评价项】评价ID:', x.id, '菜品评价数量:', x.dishEvaluations?.length || 0)
          if (x.dishEvaluations && x.dishEvaluations.length > 0) {
            console.log('【菜品评价】', x.dishEvaluations)
          }
          return {
            ...x,
            orderId: x.orderId ? String(x.orderId) : ''
          }
        })
        console.log('【评价列表】处理后的数据:', this.list)
      } catch (e) {
        console.error('【评价列表】加载失败:', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    parseImageUrls(imgUrls) {
      if (!imgUrls) return []
      if (Array.isArray(imgUrls)) return imgUrls
      try {
        const s = String(imgUrls).trim()
        if (!s) return []
        if (s.startsWith('[')) return JSON.parse(s)
        return s.split(',').map((x) => x.trim()).filter(Boolean)
      } catch (e) {
        return []
      }
    }
  }
}
</script>

<style scoped>
.page {
  padding: 24rpx;
  min-height: 100vh;
  background: #f8fafc;
  box-sizing: border-box;
}

.header {
  padding: 20rpx 0 30rpx;
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

.empty-box {
  text-align: center;
  padding: 180rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  display: block;
  margin-bottom: 24rpx;
  color: #10b981;
}

.empty-text {
  font-size: 28rpx;
  color: #64748b;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.03);
  border: 1rpx solid rgba(255,255,255,0.6);
}

.card.highlight {
  border-color: rgba(16, 185, 129, 0.7);
  box-shadow: 0 6rpx 22rpx rgba(16, 185, 129, 0.18);
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 14rpx;
}

.row.block {
  align-items: flex-start;
}

.label {
  font-size: 26rpx;
  color: #64748b;
  flex-shrink: 0;
}

.value {
  font-size: 26rpx;
  color: #1e293b;
  font-weight: 600;
}

.content {
  font-size: 26rpx;
  color: #334155;
  line-height: 1.6;
  text-align: right;
  flex: 1;
}

.divider {
  height: 1rpx;
  background: #e2e8f0;
  margin: 14rpx 0;
}

.stars {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.star {
  font-size: 30rpx;
  color: #e2e8f0;
}

.star.active {
  color: #10b981;
}

.score-text {
  margin-left: 10rpx;
  font-size: 24rpx;
  color: #10b981;
  font-weight: 700;
}

.imgs {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 6rpx;
}

.img {
  width: 140rpx;
  height: 140rpx;
  border-radius: 12rpx;
  background: #f1f5f9;
}

.reply {
  font-size: 26rpx;
  color: #0f172a;
  line-height: 1.6;
  text-align: right;
  flex: 1;
}

.reply.empty {
  color: #94a3b8;
}

/* 菜品评价区域样式 - 优化 */
.dish-section {
  margin-top: 12rpx;
  margin-bottom: 12rpx;
}

.dish-title {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
  padding-bottom: 12rpx;
  border-bottom: 2rpx solid #10b981;
}

.dish-title-icon {
  font-size: 32rpx;
  margin-right: 8rpx;
}

.dish-title-text {
  font-size: 28rpx;
  color: #10b981;
  font-weight: 700;
}

.dish-item {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border: 2rpx solid #86efac;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(16, 185, 129, 0.1);
}

.dish-item:last-child {
  margin-bottom: 0;
}

.dish-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.dish-name {
  font-size: 28rpx;
  color: #065f46;
  font-weight: 700;
}

.dish-stars {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.dish-star {
  font-size: 24rpx;
  color: #d1fae5;
}

.dish-star.active {
  color: #10b981;
}

.dish-score {
  font-size: 24rpx;
  color: #10b981;
  font-weight: 700;
  margin-left: 8rpx;
  background: #fff;
  padding: 2rpx 12rpx;
  border-radius: 12rpx;
}

.dish-content {
  font-size: 26rpx;
  color: #047857;
  line-height: 1.8;
  display: block;
  background: rgba(255, 255, 255, 0.6);
  padding: 12rpx;
  border-radius: 8rpx;
}
</style>

