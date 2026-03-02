<template>
  <view class="confirm-container">
    <!-- 收货信息区域（必填） -->
    <view class="card-item">
      <view class="card-title">收货/取餐信息</view>
      <view class="address-form">
        <input v-model="receiverName" placeholder="请输入收货人姓名" class="input-item" :disabled="fromUnpaid" />
        <input v-model="receiverPhone" placeholder="请输入联系电话" class="input-item" type="number" :disabled="fromUnpaid" />
        <input v-model="receiverAddress" placeholder="请输入取餐/收货地址" class="input-item" :disabled="fromUnpaid" />
      </view>
    </view>

    <view class="card-item">
      <view class="card-title">就餐方式</view>
      <radio-group @change="changeType" class="radio-group" :disabled="fromUnpaid">
        <label class="radio-label"><radio value="1" :checked="eatType === 1" />外带</label>
        <label class="radio-label"><radio value="2" :checked="eatType === 2" />自取</label>
      </radio-group>
    </view>

    <view class="card-item">
      <view class="card-title">菜品备注</view>
      <textarea v-model="remark" placeholder="请输入特殊要求，如：少辣、不放葱" max-length="100" class="remark-area" :disabled="fromUnpaid"></textarea>
    </view>

    <!-- 订单清单：从待支付订单跳转时隐藏（已下单无需展示） -->
    <view class="card-item" v-if="!fromUnpaid">
      <view class="card-title">订单清单</view>
      <view class="goods-list">
        <view class="goods-item" v-for="item in cartList" :key="item.id">
          <text class="goods-name">{{ item.dishName }} ×{{ item.count }}</text>
          <text class="goods-price">￥{{ (item.price * item.count).toFixed(2) }}</text>
        </view>
      </view>
    </view>

    <view class="pay-bar">
      <text class="total-price">实付：￥{{ totalPrice }}</text>
      <!-- 提交订单按钮：从待支付订单跳转时隐藏 -->
      <button class="pay-btn" @click="submitOrder" hover-class="btn-hover" :loading="isSubmitting" v-if="!fromUnpaid">
        提交订单
      </button>
    </view>

    <!-- 微信风格支付弹窗 -->
    <view class="pay-mask" v-if="showPayModal" @click="closePayModal"></view>
    <view class="pay-modal" v-if="showPayModal" :animation="animationData">
      <!-- 弹窗头部 -->
      <view class="pay-header">
        <view class="pay-title">确认支付</view>
        <view class="pay-close" @click="closePayModal">✕</view>
      </view>
      
      <!-- 金额区域 -->
      <view class="pay-amount-wrap">
        <text class="pay-amount-label">支付金额</text>
        <text class="pay-amount">￥{{ totalPrice }}</text>
      </view>
      
      <!-- 密码输入框 -->
      <view class="pwd-container">
        <text class="pwd-tip">请输入支付密码</text>
        <view class="pwd-box">
          <view class="pwd-item" v-for="i in 6" :key="i">
            <text v-if="pwd.length >= i" class="pwd-dot">●</text>
          </view>
        </view>
      </view>
      
      <!-- 数字键盘（微信风格） -->
      <view class="keyboard">
        <view class="key" v-for="num in [1,2,3,4,5,6,7,8,9]" :key="num" @click="inputPwd(num)">
          {{ num }}
        </view>
        <view class="key empty-key"></view>
        <view class="key" @click="inputPwd(0)">0</view>
        <view class="key del-key" @click="deletePwd">
          <text class="del-icon">←</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
// 导入订单API
import { submitOrder, mockPay, getOrderDetail } from '../../api/order.js'
import { getCartList, clearCartAfterOrder } from '@/utils/cartManager.js' 
const app = getApp()

export default {
  data() {
    return {
      cartList: [],
      totalPrice: '0.00',
      eatType: 1, // 1-外带，2-自取
      remark: '',
      userId: uni.getStorageSync('userId') || 1,
      receiverName: '',
      receiverPhone: '',
      receiverAddress: '',
      isSubmitting: false,
      showPayModal: false, // 支付弹窗显示状态
      pwd: '', // 模拟输入的密码
      currentOrderId: null, // 保存订单ID
      fromUnpaid: false, // 是否从待支付订单跳转
      animationData: {} // 弹窗动画
    }
  },
  async onLoad(options) {
    console.log('接收的参数：', options);
    // 判断是否从待支付订单跳转
    if (options.orderId && options.from === 'unpaid') {
      this.fromUnpaid = true;
      // 强制转为字符串，避免精度丢失
      this.currentOrderId = String(options.orderId);
      console.log('待支付订单ID（字符串）：', this.currentOrderId);
      // 加载待支付订单信息
      await this.loadUnpaidOrderInfo(this.currentOrderId);
      // 直接弹出支付弹窗
      setTimeout(() => {
        this.showPayModal = true;
        this.pwd = '';
        // 弹窗弹出动画
        this.animation.translateY(0).step();
        this.animationData = this.animation.export();
      }, 500);
      return;
    }
    
    // 正常从购物车进入：加载购物车数据
    await this.initData();
  },
  onReady() {
    // 创建弹窗动画
    this.animation = uni.createAnimation({
      duration: 300,
      timingFunction: 'ease-out'
    });
  },
  methods: {
    // 加载购物车数据（正常下单流程）
    async initData() {
      try {
        this.cartList = await getCartList(this.userId);
        this.calcTotalPrice();
      } catch (err) {
        console.error('获取购物车数据失败', err);
        uni.showToast({ title: '数据加载失败', icon: 'none' });
      }
    },
    // 计算总价
    calcTotalPrice() {
      const total = this.cartList.reduce((sum, item) => {
        return sum + (item.price || 0) * item.count;
      }, 0);
      this.totalPrice = total.toFixed(2);
    },
    // 切换就餐方式
    changeType(e) {
      this.eatType = Number(e.detail.value);
    },
    // 提交订单（正常下单流程）
    async submitOrder() {
      // 1. 前置校验（必填项）
      if (this.cartList.length === 0) {
        uni.showToast({ title: '购物车为空', icon: 'none' });
        return;
      }
      if (!this.receiverName.trim()) {
        uni.showToast({ title: '请输入收货人姓名', icon: 'none' });
        return;
      }
      if (!this.receiverPhone.trim() || !/^1[3-9]\d{9}$/.test(this.receiverPhone)) {
        uni.showToast({ title: '请输入正确的联系电话', icon: 'none' });
        return;
      }
      if (!this.receiverAddress.trim()) {
        uni.showToast({ title: '请输入取餐/收货地址', icon: 'none' });
        return;
      }

      // 2. 防止重复提交
      this.isSubmitting = true;

      try {
        // 3. 构造后端要求的参数
        const params = {
          userId: this.userId,
          receiverName: this.receiverName.trim(),
          receiverPhone: this.receiverPhone.trim(),
          receiverAddress: this.receiverAddress.trim(),
          orderRemark: this.remark.trim(),
          diningType: this.eatType,
          dishItems: this.cartList.map(item => ({
            dishId: item.dishId,
            count: item.count,
            remark: ''
          }))
        };

        // 4. 调用后端下单接口（创建订单）
        const res = await submitOrder(params);
        if (res.code !== 200) {
          throw new Error(res.msg || '订单创建失败');
        }
        // 接收字符串类型的订单ID
        this.currentOrderId = String(res.data); 
        console.log('创建订单成功，订单ID：', this.currentOrderId);
        
        // 5. 下单成功 → 弹出支付弹窗
        uni.showToast({ title: '订单创建成功', icon: 'success' });
        setTimeout(() => {
          this.showPayModal = true;
          this.pwd = '';
          // 弹窗弹出动画
          this.animation.translateY(0).step();
          this.animationData = this.animation.export();
        }, 1000);

      } catch (err) {
        console.error('订单创建失败：', err);
        uni.showToast({ title: err.msg || err.message || '订单创建失败', icon: 'none' });
      } finally {
        this.isSubmitting = false;
      }
    },
    // 加载待支付订单信息
    async loadUnpaidOrderInfo(orderId) {
      try {
        // 传字符串ID给后端
        console.log('查询订单详情，ID：', orderId);
        // 添加超时控制（5秒）
        const timeoutPromise = new Promise((_, reject) => {
          setTimeout(() => reject(new Error('加载订单超时')), 5000);
        });
        // 同时执行接口请求和超时控制
        const res = await Promise.race([
          getOrderDetail(orderId),
          timeoutPromise
        ]);
        const order = res.data;
        if (!order) {
          uni.showToast({ title: '订单不存在', icon: 'none' });
          uni.navigateBack();
          return;
        }

        // 校验订单状态（必须是待支付）
        if (order.payStatus !== 1) {
          uni.showToast({ title: '订单非待支付状态', icon: 'none' });
          uni.navigateBack();
          return;
        }
        
        // 填充订单信息
        this.receiverName = order.receiverName || '';
        this.receiverPhone = order.receiverPhone || '';
        this.receiverAddress = order.receiverAddress || '';
        this.eatType = order.diningType || 1;
        this.remark = order.orderRemark || '';
        this.totalPrice = (order.totalAmount || 0).toFixed(2);
      } catch (err) {
        console.error('加载订单信息失败：', err);
        uni.showToast({ title: err.message || '加载订单失败', icon: 'none' });
        uni.navigateBack();
      }
    },
    // 关闭支付弹窗
    closePayModal() {
      // 弹窗收起动画
      this.animation.translateY('100%').step();
      this.animationData = this.animation.export();
      // 延迟隐藏，等待动画完成
      setTimeout(() => {
        this.showPayModal = false;
        // 从待支付订单跳转时，关闭弹窗返回订单列表
        if (this.fromUnpaid) {
          uni.navigateBack();
        }
      }, 300);
    },
    // 输入支付密码
    inputPwd(num) {
      if (this.pwd.length >= 6) return;
      this.pwd += num;
      // 输入满6位 → 调用模拟支付接口
      if (this.pwd.length === 6) {
        this.handleMockPay();
      }
    },
    // 删除密码
    deletePwd() {
      if (this.pwd.length > 0) {
        this.pwd = this.pwd.substring(0, this.pwd.length - 1);
      }
    },
    // 调用模拟支付接口
    async handleMockPay() {
      console.log('调用支付接口，订单ID：', this.currentOrderId);
      if (!this.currentOrderId) {
        uni.showToast({ title: '订单ID为空', icon: 'none' });
        return;
      }
      try {
        // 传字符串ID给后端
        const res = await mockPay({ 
          orderId: String(this.currentOrderId) 
        });
        console.log('支付接口返回：', res);
        
        // 关闭支付弹窗
        this.closePayModal();
        
        // 根据支付结果跳转
        if (res.status === 'success') {
          // 支付成功：清空购物车（仅正常下单流程）
          if (!this.fromUnpaid) {
            await clearCartAfterOrder(this.userId);
          }
          uni.redirectTo({ 
            url: `/pages/order/pay-result?status=success&orderId=${this.currentOrderId}&totalPrice=${this.totalPrice}` 
          });
        } else {
          // 支付失败
          uni.redirectTo({ 
            url: `/pages/order/pay-result?status=fail&orderId=${this.currentOrderId}&totalPrice=${this.totalPrice}` 
          });
        }
      } catch (err) {
        console.error('模拟支付失败：', err);
        this.closePayModal();
        uni.showToast({ title: '支付异常', icon: 'none' });
      }
    }
  }
}
</script>

<style scoped>
.confirm-container {
  padding: 20rpx;
  background: #f7f8fa;
  min-height: 100vh;
  padding-bottom: 120rpx;
  box-sizing: border-box;
}

.card-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 24rpx;
  display: block;
}

/* 收货信息样式 */
.address-form {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.input-item {
  height: 80rpx;
  line-height: 80rpx;
  padding: 0 20rpx;
  background: #f9f9f9;
  border-radius: 12rpx;
  font-size: 28rpx;
}
.input-item:disabled {
  background: #f0f0f0;
  color: #999;
}

.radio-group {
  display: flex;
  gap: 40rpx;
}
.radio-group:disabled {
  opacity: 0.6;
}

.radio-label {
  font-size: 28rpx;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.remark-area {
  width: 100%;
  height: 140rpx;
  background: #f9f9f9;
  border-radius: 12rpx;
  padding: 20rpx;
  box-sizing: border-box;
  font-size: 26rpx;
  border: none;
}
.remark-area:disabled {
  background: #f0f0f0;
  color: #999;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.goods-item {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  font-size: 28rpx;
  color: #333;
  border-bottom: 1rpx solid #f2f2f2;
}

.goods-item:last-child {
  border-bottom: none;
}

.pay-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.1);
}

.total-price {
  font-size: 32rpx;
  color: #ff6700;
  font-weight: bold;
}

.pay-btn {
  width: 220rpx;
  height: 70rpx;
  background: #07c160;
  color: #fff;
  border: none;
  border-radius: 35rpx;
}

.btn-hover {
  opacity: 0.9;
  transform: scale(0.98);
}

/* 微信风格支付弹窗样式 */
.pay-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.7);
  z-index: 999;
  backdrop-filter: blur(2rpx); /* 毛玻璃效果 */
}

.pay-modal {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100vw;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 32rpx 32rpx 0;
  z-index: 1000;
  transform: translateY(100%); /* 默认隐藏在底部 */
  box-sizing: border-box;
}

/* 弹窗头部 */
.pay-header {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  margin-bottom: 32rpx;
}

.pay-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1d1d1f;
}

.pay-close {
  position: absolute;
  right: 0;
  font-size: 36rpx;
  color: #86868b;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 金额区域 */
.pay-amount-wrap {
  text-align: center;
  margin-bottom: 48rpx;
}

.pay-amount-label {
  font-size: 28rpx;
  color: #86868b;
  display: block;
  margin-bottom: 16rpx;
}

.pay-amount {
  font-size: 64rpx;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: 2rpx;
}

/* 密码输入区域 */
.pwd-container {
  text-align: center;
  margin-bottom: 48rpx;
}

.pwd-tip {
  font-size: 28rpx;
  color: #1d1d1f;
  display: block;
  margin-bottom: 24rpx;
}

.pwd-box {
  display: flex;
  justify-content: center;
  gap: 24rpx;
}

.pwd-item {
  width: 64rpx;
  height: 64rpx;
  border-bottom: 2rpx solid #e5e5e5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pwd-dot {
  font-size: 40rpx;
  color: #1d1d1f;
  letter-spacing: 8rpx;
}

/* 数字键盘（微信官方风格） */
.keyboard {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rpx;
  background-color: #e5e5e5;
  border-top: 1rpx solid #e5e5e5;
  margin: 0 -32rpx; /* 铺满弹窗宽度 */
}

.key {
  background: #fff;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 38rpx;
  color: #1d1d1f;
  font-weight: 500;
}

/* 空按键（对应微信键盘左下角空白） */
.empty-key {
  background: #f5f5f5;
}

/* 删除键 */
.del-key {
  background: #f5f5f5;
}

.del-icon {
  font-size: 32rpx;
  color: #86868b;
}

/* 按键点击效果 */
.key:active {
  background-color: #f0f0f0;
}
</style>