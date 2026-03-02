<template>
  <div class="merchant-order-page">
    <!-- 页面标题 + 筛选栏 -->
    <div class="page-header">
      <h2>商家订单管理</h2>
      <el-radio-group
        v-model="isCompleted"
        class="status-filter"
        @change="getOrderList"
      >
        <el-radio :label="false">未完成订单</el-radio>
        <el-radio :label="true">已完成订单</el-radio>
      </el-radio-group>
    </div>

    <!-- 订单列表 -->
    <el-table
      v-loading="loading"
      :data="orderList"
      border
      stripe
      style="width: 100%; margin-top: 20px"
      empty-text="暂无相关订单"
    >
      <el-table-column prop="order.id" label="订单ID" align="center" width="120" />
      <el-table-column prop="order.orderNo" label="订单编号" align="center" width="120" />
      <el-table-column prop="order.createTime" label="下单时间" align="center" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.order.createTime) }}
        </template>
      </el-table-column>
<el-table-column label="顾客姓名" align="center" width="120">
  <template #default="scope">
    {{ scope.row.order.receiverName || '未知顾客' }}
  </template>
</el-table-column>
<el-table-column label="联系电话" align="center" width="150">
  <template #default="scope">
    {{ scope.row.order.receiverPhone || '未知电话' }}
  </template>
</el-table-column>
      <el-table-column label="订单商品" align="center" min-width="200">
        <template #default="scope">
          <div v-for="item in scope.row.orderItemList" :key="item.id" class="order-item">
            {{ item.dishName }} × {{ item.quantity }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="order.totalAmount" label="订单金额(元)" align="center" width="120">
        <template #default="scope">
          {{ scope.row.order.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="order.orderStatus" label="订单状态" align="center" width="120">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.order.orderStatus)">{{ getStatusText(scope.row.order.orderStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <!-- 仅未完成订单显示操作按钮 -->
          <template v-if="[1, 2].includes(scope.row.order.orderStatus)">
            <el-button
              type="primary"
              size="small"
              @click="handleComplete(scope.row.order.id)"
              :loading="operateLoading[scope.row.order.id]"
            >
              完成订单
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleCancel(scope.row.order.id)"
              style="margin-left: 10px"
              :loading="operateLoading[scope.row.order.id]"
            >
              取消订单
            </el-button>
          </template>
          <!-- 已取消订单（4）：单独显示【恢复订单】按钮 -->
          <template v-else-if="scope.row.order.orderStatus === 4">
            <el-button
              type="success"
              size="small"
              @click="handleRecover(scope.row.order.id)"
              :loading="operateLoading[scope.row.order.id]"
            >
              恢复订单
            </el-button>
          </template>
          <span v-else>无操作</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 取消订单弹窗：输入取消原因 -->
    <el-dialog
      title="取消订单"
      v-model="cancelDialogVisible"
      width="400px"
      @close="resetCancelForm"
    >
      <el-form :model="cancelForm" label-width="80px" ref="cancelFormRef">
        <el-form-item
          label="取消原因"
          prop="cancelReason"
          required
        >
          <el-input
            v-model="cancelForm.cancelReason"
            type="textarea"
            rows="3"
            placeholder="请输入订单取消原因（必填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmCancel"
          :loading="cancelLoading"
        >
          确认取消
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatTime } from '@/utils/dateUtil'
// 导入订单模块API方法
import { getMerchantOrderList, completeMerchantOrder, cancelMerchantOrder, recoverMerchantOrder } from '@/api/order'

// ---------------------- 状态管理 ----------------------
const isCompleted = ref(false)
const orderList = ref([])
const loading = ref(false)
const operateLoading = ref({})
const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelForm = reactive({
  orderId: '',
  cancelReason: ''
})
const cancelFormRef = ref(null)
const refreshTimer = ref(null)

// ---------------------- 分页参数 ----------------------
const queryParams = reactive({
  page: 1,
  pageSize: 10
})
const total = ref(0)

// ---------------------- 页面初始化 ----------------------
onMounted(() => {
  getOrderList()
  startAutoRefresh()
})

onUnmounted(() => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
  }
})

// ---------------------- 核心方法：查询订单列表 ----------------------
const getOrderList = async () => {
  loading.value = true
  try {
    // ✅ 修复：将布尔值转换为后端能识别的 0 或 1
    const statusValue = isCompleted.value ? 1 : 0;

    const params = {
      isCompleted: statusValue, // 发送 0 或 1，而不是 true/false
      page: Number(queryParams.page),      // 确保是数字
      pageSize: Number(queryParams.pageSize) // 确保是数字
    }
    
    // 调试：看看发送的参数对不对
    console.log('🚀 发送请求参数:', params) 

    const res = await getMerchantOrderList(params)
    
    if (res.code === 200) {
      // 适配后端返回的 IPage 结构
      orderList.value = res.data?.records || res.data || []
      total.value = res.data?.total || orderList.value.length
      
      // 避免频繁提示
      if (refreshTimer.value === null || orderList.value.length > 0) {
        // ElMessage.success(res.msg || '订单查询成功') // 演示时可注释掉，避免刷屏
      }
    } else {
      ElMessage.error(res.msg || '订单查询失败')
      orderList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('订单查询失败：', error)
    ElMessage.error('网络异常，订单查询失败')
    orderList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
// ----------------------分页事件处理 ----------------------
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.page = 1  // 每页条数改变时，重置到第一页
  getOrderList()
}

const handleCurrentChange = (val) => {
  queryParams.page = val
  getOrderList()
}

// ---------------------- 核心方法：完成订单（调用API） ----------------------
const handleComplete = async (orderId) => {
  const confirm = await ElMessageBox.confirm(
    '确定要标记该订单为已完成吗？',
    '操作确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  if (!confirm) return

  operateLoading.value[orderId] = true
  try {
    // 调用完成订单API
    const res = await completeMerchantOrder({ orderId })
    if (res.code === 200) {
      ElMessage.success(res.msg || '订单完成成功')
      getOrderList()
    } else {
      ElMessage.error(res.msg || '订单完成失败')
    }
  } catch (error) {
    console.error('完成订单失败：', error)
    ElMessage.error('网络异常，订单完成失败')
  } finally {
    operateLoading.value[orderId] = false
  }
}

// ---------------------- 核心方法：取消订单（调用API） ----------------------
const handleCancel = (orderId) => {
  cancelForm.orderId = orderId
  cancelDialogVisible.value = true
}

const resetCancelForm = () => {
  cancelForm.cancelReason = ''
  if (cancelFormRef.value) {
    cancelFormRef.value.clearValidate()
  }
}

const confirmCancel = async () => {
  if (!cancelForm.cancelReason.trim()) {
    ElMessage.warning('请输入取消原因')
    return
  }

  cancelLoading.value = true
  try {
    // 调用取消订单API，传递orderId和cancelReason
    const res = await cancelMerchantOrder({
      orderId: cancelForm.orderId,
      cancelReason: cancelForm.cancelReason.trim()
    })
    if (res.code === 200) {
      ElMessage.success(res.msg || '订单取消成功')
      cancelDialogVisible.value = false
      getOrderList()
    } else {
      ElMessage.error(res.msg || '订单取消失败')
    }
  } catch (error) {
    console.error('取消订单失败：', error)
    ElMessage.error('网络异常，订单取消失败')
  } finally {
    cancelLoading.value = false
  }
}

// ---------------------- 恢复订单方法 ----------------------
const handleRecover = async (orderId) => {
  const confirm = await ElMessageBox.confirm(
    '确定要恢复该订单为待接单状态吗？',
    '恢复订单确认',
    {
      confirmButtonText: '确定恢复',
      cancelButtonText: '取消',
      type: 'question'
    }
  )
  if (!confirm) return

  operateLoading.value[orderId] = true
  try {
    // 调用后端恢复订单API，将状态从4（已取消）改为1（待接单）
    const res = await recoverMerchantOrder({ orderId })
    if (res.code === 200) {
      ElMessage.success(res.msg || '订单恢复成功，已归为未完成订单')
      getOrderList() // 刷新列表，恢复后自动显示未完成订单的按钮
    } else {
      ElMessage.error(res.msg || '订单恢复失败')
    }
  } catch (error) {
    console.error('恢复订单失败：', error)
    ElMessage.error('网络异常，订单恢复失败')
  } finally {
    operateLoading.value[orderId] = false
  }
}

// ---------------------- 辅助方法：订单状态处理 ----------------------
const getStatusText = (status) => {
  switch (status) {
    case 1: return '待接单'
    case 2: return '待取餐'
    case 3: return '已完成'
    case 4: return '已取消'
    default: return '未知状态'
  }
}
const getStatusTagType = (status) => {
  switch (status) {
    case 1: return 'warning' // 待接单-橙色（未完成）
    case 2: return 'info'    // 待取餐-蓝色（未完成）
    case 3: return 'success' // 已完成-绿色
    case 4: return 'danger'  // 已取消-红色
    default: return ''
  }
}

// ---------------------- 自动刷新功能 ----------------------
const startAutoRefresh = () => {
  if (refreshTimer.value) clearInterval(refreshTimer.value)
  refreshTimer.value = setInterval(() => {
    if (!isCompleted.value) {
      getOrderList()
    }
  }, 30000)
}

// ---------------------- 监听筛选条件变化 ----------------------
watch(isCompleted, () => {
  queryParams.page = 1  // 切换完成状态时，重置到第一页
  startAutoRefresh()
})
</script>

<style scoped>
.merchant-order-page {
  padding: 20px;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e6e6e6;
  padding-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.status-filter {
  font-size: 14px;
}

.order-item {
  line-height: 24px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding: 20px 0;
}

:deep(.el-table__cell) {
  padding: 12px 0 !important;
}
</style>