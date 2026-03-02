<template>
  <view class="debug-page">
    <view class="header">
      <text class="title">评价数据调试页面</text>
    </view>

    <view class="section">
      <text class="section-title">用户ID</text>
      <text class="section-content">{{ userId }}</text>
    </view>

    <view class="section">
      <button class="btn" @click="loadData">加载评价数据</button>
    </view>

    <view class="section" v-if="loading">
      <text class="loading-text">加载中...</text>
    </view>

    <view class="section" v-if="rawData">
      <text class="section-title">原始数据（JSON）</text>
      <view class="json-box">
        <text class="json-text">{{ rawData }}</text>
      </view>
    </view>

    <view class="section" v-if="evaluationList.length > 0">
      <text class="section-title">评价列表（{{ evaluationList.length }}条）</text>
      <view class="eval-item" v-for="(item, index) in evaluationList" :key="index">
        <view class="eval-row">
          <text class="label">评价ID:</text>
          <text class="value">{{ item.id }}</text>
        </view>
        <view class="eval-row">
          <text class="label">订单号:</text>
          <text class="value">{{ item.orderNo }}</text>
        </view>
        <view class="eval-row">
          <text class="label">评分:</text>
          <text class="value">{{ item.score }}</text>
        </view>
        <view class="eval-row">
          <text class="label">评价内容:</text>
          <text class="value">{{ item.content }}</text>
        </view>
        <view class="eval-row">
          <text class="label">菜品评价数量:</text>
          <text class="value highlight">{{ item.dishEvaluations ? item.dishEvaluations.length : 0 }}</text>
        </view>
        <view v-if="item.dishEvaluations && item.dishEvaluations.length > 0" class="dish-list">
          <text class="dish-title">菜品评价详情:</text>
          <view class="dish-item" v-for="(dish, dIndex) in item.dishEvaluations" :key="dIndex">
            <text class="dish-info">{{ dIndex + 1 }}. {{ dish.dishName }} - {{ dish.score }}分</text>
            <text class="dish-content">{{ dish.content }}</text>
          </view>
        </view>
        <view v-else class="no-dish">
          <text class="no-dish-text">❌ 没有菜品评价数据</text>
        </view>
      </view>
    </view>

    <view class="section" v-if="error">
      <text class="error-text">错误: {{ error }}</text>
    </view>
  </view>
</template>

<script>
import { getUserEvaluations } from '../../api/evaluation.js'

export default {
  data() {
    return {
      userId: '',
      loading: false,
      rawData: '',
      evaluationList: [],
      error: ''
    }
  },
  onLoad() {
    this.userId = uni.getStorageSync('userId') || '未登录'
  },
  methods: {
    async loadData() {
      this.loading = true
      this.error = ''
      this.rawData = ''
      this.evaluationList = []
      
      try {
        console.log('【调试】开始加载评价数据...')
        console.log('【调试】用户ID:', this.userId)
        
        const res = await getUserEvaluations()
        
        console.log('【调试】接口响应:', res)
        
        // 保存原始数据
        this.rawData = JSON.stringify(res, null, 2)
        
        if (res.code === 200) {
          this.evaluationList = res.data || []
          console.log('【调试】评价列表:', this.evaluationList)
          
          // 检查每个评价的菜品评价
          this.evaluationList.forEach((item, index) => {
            console.log(`【调试】评价 ${index + 1}:`, {
              id: item.id,
              orderNo: item.orderNo,
              dishEvaluationsCount: item.dishEvaluations ? item.dishEvaluations.length : 0,
              dishEvaluations: item.dishEvaluations
            })
          })
        } else {
          this.error = res.msg || '获取数据失败'
        }
      } catch (err) {
        console.error('【调试】加载失败:', err)
        this.error = err.message || '网络错误'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.debug-page {
  padding: 20rpx;
  background: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  border-radius: 10rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.section {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  border-radius: 10rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.section-content {
  font-size: 26rpx;
  color: #666;
}

.btn {
  background: #07c160;
  color: #fff;
  border: none;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
}

.loading-text {
  font-size: 26rpx;
  color: #999;
  text-align: center;
  display: block;
}

.json-box {
  background: #f5f5f5;
  padding: 20rpx;
  border-radius: 10rpx;
  max-height: 600rpx;
  overflow-y: auto;
}

.json-text {
  font-size: 22rpx;
  color: #333;
  font-family: monospace;
  word-break: break-all;
  white-space: pre-wrap;
}

.eval-item {
  background: #f9f9f9;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 10rpx;
  border: 2rpx solid #e0e0e0;
}

.eval-row {
  display: flex;
  margin-bottom: 10rpx;
}

.label {
  font-size: 24rpx;
  color: #666;
  width: 200rpx;
}

.value {
  font-size: 24rpx;
  color: #333;
  flex: 1;
}

.value.highlight {
  color: #07c160;
  font-weight: bold;
  font-size: 28rpx;
}

.dish-list {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #e0e0e0;
}

.dish-title {
  font-size: 26rpx;
  color: #07c160;
  font-weight: bold;
  display: block;
  margin-bottom: 15rpx;
}

.dish-item {
  background: #fff;
  padding: 15rpx;
  margin-bottom: 10rpx;
  border-radius: 8rpx;
  border-left: 4rpx solid #07c160;
}

.dish-info {
  font-size: 24rpx;
  color: #333;
  font-weight: bold;
  display: block;
  margin-bottom: 8rpx;
}

.dish-content {
  font-size: 22rpx;
  color: #666;
  display: block;
}

.no-dish {
  margin-top: 20rpx;
  padding: 20rpx;
  background: #fff3cd;
  border-radius: 8rpx;
  text-align: center;
}

.no-dish-text {
  font-size: 24rpx;
  color: #856404;
}

.error-text {
  font-size: 26rpx;
  color: #f56c6c;
  display: block;
}
</style>
