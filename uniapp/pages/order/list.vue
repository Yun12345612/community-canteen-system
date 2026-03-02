<template>
  <view class="order-container">
    <!-- 空订单状态 -->
    <view class="empty-box" v-if="orderList.length === 0 && !loading">
      <text class="empty-icon">📋</text>
      <text class="empty-text">暂无订单记录，快去点餐吧~</text>
      <button class="goto-btn" @click="goToDish" hover-class="btn-hover">立即点餐</button>
    </view>

    <!-- 订单列表 -->
    <view class="order-list" v-else-if="!loading">
      <view class="order-item" v-for="item in orderList" :key="item.id">
        <!-- 订单头部：编号+支付状态+订单状态 -->
        <view class="order-header">
          <text class="order-no">订单编号：{{ item.orderNo }}</text>
          <view class="status-group">
            <!-- 支付状态标签 -->
            <text class="pay-status" :class="getPayStatusClass(item.payStatus)">
              {{ getPayStatusText(item.payStatus) }}
            </text>
            <!-- 订单状态标签 -->
            <text class="order-status" :class="getStatusClass(item.orderStatus)">
              {{ getStatusText(item.orderStatus) }}
            </text>
          </view>
        </view>

        <!-- 订单信息 -->
        <view class="order-info">
          <view class="info-item">
            <text class="label">就餐方式：</text>
            <text class="value">{{ item.diningType === 1 ? '外带' : '自取' }}</text>
          </view>
          <view class="info-item">
            <text class="label">收货人：</text>
            <text class="value">{{ item.receiverName }} {{ item.receiverPhone }}</text>
          </view>
          <view class="info-item">
            <text class="label">收货地址：</text>
            <text class="value">{{ item.receiverAddress }}</text>
          </view>
          <view class="info-item">
            <text class="label">下单时间：</text>
            <text class="value">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>

        <!-- 订单金额 -->
        <view class="order-amount">
          <text class="amount-text">实付金额：¥{{ (item.totalAmount || 0).toFixed(2) }}</text>
        </view>

<!-- 订单操作按钮（根据状态显示） -->
        <!-- payStatus: 1=待支付, 2=已支付 -->
        <!-- orderStatus: 1=待接单, 2=待取餐, 3=已完成, 4=已取消 -->
        <view class="order-actions">
          <!-- 待支付：去支付 + 取消 -->
          <template v-if="item.payStatus === 1">
            <button class="action-btn pay-btn" @click="goToPay(item.id)">去支付</button>
            <button class="action-btn cancel-btn" @click="cancelOrder(item.id)">取消订单</button>
          </template>
          <!-- 已支付待取餐：无操作 -->
          <template v-else-if="item.payStatus === 2 && item.orderStatus === 2">
            <text class="status-tip">等待商家出餐...</text>
          </template>
          <!-- 已完成订单：显示"去评价"或"查看评价"按钮 + 菜品评价 -->
          <template v-else-if="item.orderStatus === 3">
            <template v-if="!item.isEvaluating">
              <button
                class="action-btn evaluate-btn"
                :class="{ 'evaluated': item.isEvaluated }"
                @click="goToEvaluation(item.id, item)"
              >
                {{ item.isEvaluated ? '查看评价' : '去评价' }}
              </button>
              <button
                class="action-btn dish-eval-btn"
                @click="goToDishEvaluation(item.id)"
              >
                菜品评价
              </button>
            </template>
            <text v-else class="status-tip">检查评价状态...</text>
          </template>
          <!-- 已取消订单 -->
          <template v-else-if="item.orderStatus === 4">
            <text class="status-tip cancelled">订单已取消</text>
          </template>
        </view>
      </view>
    </view>

    <!-- 加载中 -->
    <view class="loading-box" v-if="loading">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 底部导航栏：current-index=2 代表订单页选中 -->
    <custom-tabbar :current-index="2"></custom-tabbar>
  </view>
</template>

<script>
import { getOrderList } from '../../api/order.js'
// 1. 导入评价校验接口
import { canEvaluate } from '../../api/evaluation.js'
// 引入底部导航栏组件
import customTabbar from "@/components/custom-tabbar/custom-tabbar.vue"

export default {
  // 注册导航栏组件
  components: {
    customTabbar
  },
  data() {
    return {
      orderList: [],
      loading: false,
      userId: uni.getStorageSync('userId') || 1 // 从缓存取用户ID
    }
  },
  onShow() {
    // 每次进入页面都刷新订单列表
    this.getOrderData()
  },
  methods: {
    // 获取订单列表
    async getOrderData() {
      this.loading = true
      try {
        const res = await getOrderList(this.userId)
        this.orderList = res.data || []
        
        // 2. 对已完成的订单，批量校验是否可评价
        for (let item of this.orderList) {
          if (item.orderStatus === 3) { // 仅已完成订单校验
            item.isEvaluating = true // 标记加载中
            try {
              // 调用校验接口（传递字符串类型orderId）
              const evalRes = await canEvaluate(String(item.id))
              item.isEvaluated = !evalRes.data // 可评价=未评价 → isEvaluated=false
            } catch (err) {
              console.error(`校验订单${item.id}可评价状态失败：`, err)
              item.isEvaluated = true // 异常时默认标记为已评价
            } finally {
              item.isEvaluating = false // 取消加载标记
            }
          } else {
            item.isEvaluated = true // 非已完成订单，默认不可评价
          }
        }
      } catch (err) {
        console.error('获取订单列表失败：', err)
        uni.showToast({ title: '获取订单失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    // 跳转到点餐页面
    goToDish() {
      uni.reLaunch({ url: '/pages/dish/list' })
    },
    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return ''
      return timeStr.replace('T', ' ').substring(0, 16)
    },
    // 支付状态文字
    getPayStatusText(payStatus) {
      const payStatusMap = {
        1: '待支付',
        2: '已支付'
      }
      return payStatusMap[payStatus] || '未知状态'
    },
    // 支付状态样式类
    getPayStatusClass(payStatus) {
      switch (payStatus) {
        case 1: return 'pay-wait';
        case 2: return 'pay-success';
        default: return ''
      }
    },
    // 订单状态文字
    getStatusText(orderStatus) {
      const statusMap = {
        1: '待接单',
        2: '待取餐',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[orderStatus] || '未知状态'
    },
    // 订单状态样式类
    getStatusClass(orderStatus) {
      switch (orderStatus) {
        case 1: return 'order-wait';
        case 2: return 'order-processing';
        case 3: return 'order-success';
        case 4: return 'order-cancel';
        default: return ''
      }
    },
    // 去支付
    goToPay(orderId) {
      const strOrderId = String(orderId);
      uni.navigateTo({ 
        url: `/pages/order/confirm?orderId=${strOrderId}&from=unpaid` 
      })
    },
    // 取消订单
    cancelOrder(orderId) {
      uni.showModal({
        title: '提示',
        content: '确定取消该订单吗？',
        success: async (res) => {
          if (res.confirm) {
            uni.showToast({ title: '订单已取消', icon: 'success' })
            this.getOrderData()
          }
        }
      })
    },
    // 3. 跳转到评价页面
    goToEvaluation(orderId, item) {
      const strOrderId = String(orderId);
      // 如果已评价，跳转到评价列表页查看
      if (item && item.isEvaluated) {
        uni.navigateTo({
          url: `/pages/evaluation/list?orderId=${strOrderId}`
        })
        return
      }
      // 如果未评价，跳转到评价页面进行评价
      uni.navigateTo({
        url: `/pages/order/evaluation?orderId=${strOrderId}`
      })
    },
    // 4. 跳转到菜品评价页面
    goToDishEvaluation(orderId) {
      const strOrderId = String(orderId);
      uni.navigateTo({
        url: `/pages/order/dish-evaluation?orderId=${strOrderId}`
      })
    }
  }
}
</script>

<style scoped>
/* 原有样式不变，新增“去评价”按钮样式 */
.order-container {
  padding: 20rpx;
  min-height: 100vh;
  background: #f7f8fa;
  box-sizing: border-box;
  /* 增加底部内边距，避免内容被导航栏遮挡 */
  padding-bottom: 60rpx;
}

/* 空订单样式 */
.empty-box {
  text-align: center;
  padding-top: 150rpx;
}
.empty-icon {
  font-size: 110rpx;
  display: block;
  margin-bottom: 24rpx;
  color: #ccc;
}
.empty-text {
  font-size: 28rpx;
  color: #999;
  display: block;
  margin-bottom: 48rpx;
}
.goto-btn {
  width: 260rpx;
  height: 76rpx;
  background: #ff6700;
  color: #fff;
  border: none;
  border-radius: 38rpx;
  font-size: 28rpx;
}

/* 加载中 */
.loading-box {
  text-align: center;
  padding: 100rpx 0;
}
.loading-text {
  font-size: 28rpx;
  color: #999;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.order-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f2f2f2;
}
.order-no {
  font-size: 26rpx;
  color: #666;
}
.status-group {
  display: flex;
  gap: 10rpx;
  align-items: center;
}
.pay-status {
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  color: #fff;
}
.pay-wait { background: #ff3b30; }
.pay-success { background: #07c160; }
.pay-refund { background: #999; }
.order-status {
  font-size: 26rpx;
  font-weight: bold;
}
.order-wait { color: #ff9500; }
.order-processing { color: #0088ff; }
.order-success { color: #07c160; }
.order-cancel { color: #999; }

/* 订单信息 */
.order-info {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 20rpx;
}
.info-item {
  display: flex;
  font-size: 28rpx;
}
.label {
  color: #999;
  width: 120rpx;
}
.value {
  color: #333;
  flex: 1;
}

/* 订单金额 */
.order-amount {
  padding-top: 20rpx;
  border-top: 1rpx solid #f2f2f2;
}
.amount-text {
  font-size: 30rpx;
  color: #ff6700;
  font-weight: bold;
}

/* 订单操作按钮 */
.order-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 20rpx;
}
.action-btn {
  flex: 1;
  height: 70rpx;
  border: none;
  border-radius: 35rpx;
  font-size: 26rpx;
}
.pay-btn {
  background: #07c160;
  color: #fff;
}
.cancel-btn {
  background: #f5f5f5;
  color: #666;
}
/* 去评价按钮样式 */
.evaluate-btn {
  background: #ff6700;
  color: #fff;
}
.evaluate-btn.evaluated {
  background: #07c160;
  color: #fff;
}
.evaluate-btn:disabled {
  background: #ccc;
  color: #fff;
}
/* 菜品评价按钮样式 */
.dish-eval-btn {
  background: #667eea;
  color: #fff;
}

/* 状态提示文字 */
.status-tip {
  font-size: 26rpx;
  color: #999;
  line-height: 70rpx;
}
.status-tip.cancelled {
  color: #999;
}

.btn-hover {
  opacity: 0.9;
  transform: scale(0.98);
}
</style>
