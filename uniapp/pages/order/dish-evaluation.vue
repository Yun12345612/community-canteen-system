<template>
  <view class="dish-evaluation-container">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">
        <text class="back-icon">←</text>
      </view>
      <text class="nav-title">菜品评价</text>
    </view>

    <!-- 订单信息 -->
    <view class="order-info">
      <text class="order-no">订单号：{{ orderNo }}</text>
    </view>

    <!-- 已评价提示 -->
    <view class="evaluated-tip" v-if="hasEvaluated">
      <text class="tip-text">您已评价过该订单的菜品，以下是您的评价</text>
    </view>

    <!-- 菜品列表 -->
    <view class="dish-list">
      <view class="dish-item" v-for="(dish, index) in dishList" :key="index">
        <!-- 菜品信息 -->
        <view class="dish-info">
          <image class="dish-image" :src="dish.dishImage || '/static/default-dish.png'" mode="aspectFill"></image>
          <view class="dish-details">
            <text class="dish-name">{{ dish.dishName }}</text>
            <text class="dish-price">¥{{ dish.price }} × {{ dish.quantity }}</text>
          </view>
        </view>

        <!-- 评分 -->
        <view class="rating-section">
          <text class="section-label">评分：</text>
          <view class="stars">
            <text
              class="star"
              v-for="star in 5"
              :key="star"
              @click="setRating(index, star)"
            >
              {{ star <= dish.score ? '★' : '☆' }}
            </text>
          </view>
        </view>

        <!-- 评价内容 -->
        <view class="content-section">
          <textarea
            class="content-input"
            v-model="dish.content"
            placeholder="说说这道菜的口味、分量等（选填）"
            maxlength="200"
            :disabled="hasEvaluated"
          ></textarea>
          <text class="char-count">{{ dish.content?.length || 0 }}/200</text>
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-section">
      <button class="submit-btn" @click="submitEvaluation" :disabled="submitting || hasEvaluated">
        {{ hasEvaluated ? '已评价' : (submitting ? '提交中...' : '提交评价') }}
      </button>
    </view>
  </view>
</template>

<script>
import { getOrderDetail } from '../../api/order.js'
import { submitDishEvaluation, getDishEvaluationsByOrderId } from '../../api/evaluation.js'

export default {
  data() {
    return {
      orderId: '',
      orderNo: '',
      dishList: [],
      submitting: false,
      hasEvaluated: false
    }
  },
  onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId
      this.loadOrderDishes()
    } else {
      uni.showToast({
        title: '订单ID不能为空',
        icon: 'none'
      })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }
  },
  methods: {
    // 加载订单菜品
    async loadOrderDishes() {
      try {
        uni.showLoading({ title: '加载中...' })

        // 并行加载订单详情和已评价数据
        const [orderRes, evalRes] = await Promise.all([
          getOrderDetail(this.orderId),
          getDishEvaluationsByOrderId(this.orderId)
        ])

        console.log('订单详情返回数据:', JSON.stringify(orderRes))
        console.log('菜品评价返回数据:', JSON.stringify(evalRes))

        if (orderRes.code === 200 && orderRes.data) {
          this.orderNo = orderRes.data.orderNo
          const items = orderRes.data.dishItems || orderRes.data.items || []

          // 构建菜品列表
          this.dishList = items.map(item => ({
            orderItemId: item.id || item.orderItemId,
            dishId: item.dishId,
            dishName: item.dishName,
            dishImage: item.dishImage,
            price: item.unitPrice || item.price,
            quantity: item.count || item.quantity,
            score: 5,
            content: ''
          }))

          // 如果有已评价数据，回显
          if (evalRes.code === 200 && evalRes.data && evalRes.data.length > 0) {
            this.hasEvaluated = true
            console.log('开始匹配菜品评价数据')
            // 根据 orderItemId 匹配回显数据
            evalRes.data.forEach(evalItem => {
              console.log('评价数据项:', JSON.stringify(evalItem))
              // 转换为字符串进行比较，避免类型不匹配问题
              const evalOrderItemId = String(evalItem.orderItemId)
              const dish = this.dishList.find(d => String(d.orderItemId) === evalOrderItemId)
              console.log('匹配结果 - evalOrderItemId:', evalOrderItemId, '找到:', !!dish)
              if (dish) {
                dish.score = evalItem.score
                dish.content = evalItem.content || ''
                console.log('回显成功 - dishName:', dish.dishName, 'score:', dish.score)
              }
            })
          }

          if (this.dishList.length === 0) {
            uni.showToast({
              title: '该订单暂无菜品信息',
              icon: 'none'
            })
          }
        } else {
          throw new Error(orderRes.msg || '加载订单失败')
        }
      } catch (error) {
        console.error('加载订单菜品失败:', error)
        uni.showToast({
          title: error.message || '加载失败',
          icon: 'none'
        })
      } finally {
        uni.hideLoading()
      }
    },

    // 设置评分
    setRating(index, score) {
      if (!this.hasEvaluated) {
        this.dishList[index].score = score
      }
    },

    // 提交评价
    async submitEvaluation() {
      if (this.hasEvaluated) {
        uni.showToast({
          title: '您已评价过',
          icon: 'none'
        })
        return
      }

      // 验证是否有菜品
      if (!this.dishList || this.dishList.length === 0) {
        uni.showToast({
          title: '暂无菜品信息，请稍后重试',
          icon: 'none'
        })
        return
      }

      // 验证菜品数据完整性
      const invalidDish = this.dishList.find(dish => !dish.orderItemId || !dish.dishId)
      if (invalidDish) {
        console.error('菜品数据不完整:', invalidDish)
        uni.showToast({
          title: '菜品数据异常，请重新进入',
          icon: 'none'
        })
        return
      }

      this.submitting = true

      try {
        const evaluationData = {
          orderId: this.orderId,
          dishEvaluations: this.dishList.map(dish => ({
            orderItemId: dish.orderItemId,
            dishId: dish.dishId,
            score: dish.score,
            content: dish.content || ''
          }))
        }

        console.log('提交菜品评价数据:', JSON.stringify(evaluationData))
        const res = await submitDishEvaluation(evaluationData)

        if (res.code === 200) {
          this.hasEvaluated = true
          uni.showToast({
            title: '评价成功',
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } else {
          throw new Error(res.msg || '提交失败')
        }
      } catch (error) {
        console.error('提交评价失败:', error)
        uni.showToast({
          title: error.message || '提交失败',
          icon: 'none'
        })
      } finally {
        this.submitting = false
      }
    },

    // 返回
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.dish-evaluation-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 100rpx;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 88rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1rpx solid #eee;
  z-index: 999;
}

.nav-back {
  position: absolute;
  left: 20rpx;
  padding: 10rpx;
}

.back-icon {
  font-size: 40rpx;
  color: #333;
}

.nav-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
}

.order-info {
  margin-top: 88rpx;
  padding: 20rpx 30rpx;
  background-color: #fff;
}

.order-no {
  font-size: 28rpx;
  color: #666;
}

.evaluated-tip {
  padding: 20rpx 30rpx;
  background-color: #fff7e6;
  border-bottom: 1rpx solid #ffd591;
}

.tip-text {
  font-size: 26rpx;
  color: #fa8c16;
}

.dish-list {
  margin-top: 20rpx;
}

.dish-item {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.dish-info {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.dish-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.dish-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dish-name {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.dish-price {
  font-size: 26rpx;
  color: #999;
}

.rating-section {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-label {
  font-size: 28rpx;
  color: #333;
  margin-right: 20rpx;
}

.stars {
  display: flex;
}

.star {
  font-size: 48rpx;
  color: #ffb400;
  margin-right: 10rpx;
}

.content-section {
  position: relative;
}

.content-input {
  width: 100%;
  min-height: 150rpx;
  padding: 20rpx;
  background-color: #f8f8f8;
  border-radius: 8rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.content-input[disabled] {
  background-color: #f0f0f0;
  color: #666;
}

.char-count {
  position: absolute;
  right: 20rpx;
  bottom: 10rpx;
  font-size: 24rpx;
  color: #999;
}

.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 30rpx;
  background-color: #fff;
  border-top: 1rpx solid #eee;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 32rpx;
  border-radius: 44rpx;
  border: none;
}

.submit-btn[disabled] {
  opacity: 0.6;
  background: #ccc;
}
</style>
