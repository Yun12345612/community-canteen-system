<template>
  <!-- 页面全局容器，与后台主页面样式统一 -->
  <div class="holiday-setting-page">
    <!-- 卡片布局，贴合后台现有el-card风格 -->
    <el-card shadow="hover" class="main-card">
      <h3 class="page-title">店铺营业状态设置</h3>
      <!-- 休假设置核心表单，绑定数据/校验/按钮 -->
      <el-form
        :model="holidayForm"
        :rules="holidayRules"
        ref="formRef"
        label-width="120px"
        class="holiday-form"
      >
        <!-- 营业状态选择：0-恢复营业，1-设置休假 -->
        <el-form-item label="营业状态" prop="isHoliday">
          <el-radio-group v-model="holidayForm.isHoliday">
            <el-radio label="0">恢复营业</el-radio>
            <el-radio label="1">设置休假</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 休假开始时间：仅休假状态显示，限制格式为yyyy-MM-dd -->
        <el-form-item label="休假开始时间" prop="holidayStart" v-if="holidayForm.isHoliday === '1'">
          <el-date-picker
            v-model="holidayForm.holidayStart"
            type="date"
            placeholder="选择开始日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
            :disabled-date="disabledBeforeToday"
          />
        </el-form-item>

        <!-- 休假结束时间：仅休假状态显示，需晚于开始时间 -->
        <el-form-item label="休假结束时间" prop="holidayEnd" v-if="holidayForm.isHoliday === '1'">
          <el-date-picker
            v-model="holidayForm.holidayEnd"
            type="date"
            placeholder="选择结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
            :disabled-date="(time) => disabledBeforeStart(time, holidayForm.holidayStart)"
          />
        </el-form-item>

        <!-- 提交/重置按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">确认提交</el-button>
          <el-button type="default" @click="handleReset" style="margin-left: 10px;">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
// 导入Vue3核心API
import { ref, reactive } from 'vue'
// 导入Element Plus提示组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 导入商家接口，关联setHoliday休假设置接口
// 按需导入需要的函数，和dish.js/admin.js调用风格一致
import { merchantLogin, merchantRegister, merchantSetHoliday } from '@/api/merchant'

// 1. 表单基础配置：Ref标识（用于校验）、加载状态（提交时按钮置灰）
const formRef = ref(null)
const loading = ref(false)

// 2. 核心表单数据：与后端setHoliday接口参数完全一致，修复类型+真实merchantId
const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}')
const holidayForm = reactive({
  merchantId: merchantInfo.id || 4, // 优先从缓存取真实ID，无则用测试值4
  isHoliday: '0',  // 改为字符串，匹配单选框值，0=恢复营业，1=设置休假
  holidayStart: '', // 休假开始时间（yyyy-MM-dd，接口要求格式）
  holidayEnd: ''   // 休假结束时间（yyyy-MM-dd，接口要求格式）
})

// 3. 日期校验方法：结束时间必须晚于开始时间（表单自定义校验）
const validateEndAfterStart = (rule, value, callback) => {
  if (!value) return callback()
  if (!holidayForm.holidayStart) {
    callback(new Error('请先选择休假开始时间'))
    return
  }
  const start = new Date(holidayForm.holidayStart)
  const end = new Date(value)
  if (end < start) {
    callback(new Error('休假结束时间不能早于开始时间'))
  } else {
    callback()
  }
}

// 4. 时间选择器禁用：禁止选择今天之前的日期
const disabledBeforeToday = (time) => {
  return time < new Date(Date.now() - 8.64e7) // 8.64e7=24小时毫秒数，禁用昨天及之前
}

// 5. 时间选择器禁用：结束时间禁止选择开始时间之前的日期
const disabledBeforeStart = (time, startDate) => {
  if (!startDate) return true
  return time < new Date(startDate)
}

// 6. 完善表单校验规则：针对性校验，避免空数据提交
// 此时所有被引用的函数均已提前定义，无未初始化问题
const holidayRules = reactive({
  isHoliday: [
    { required: true, message: '请选择店铺营业状态', trigger: 'change' }
  ],
  holidayStart: [
    { required: true, message: '请选择休假开始时间', trigger: 'blur' }
  ],
  holidayEnd: [
    { required: true, message: '请选择休假结束时间', trigger: 'blur' },
    { validator: validateEndAfterStart, trigger: 'blur' }
  ]
})

// 7. 表单提交方法：校验+二次确认+调用接口
const handleSubmit = async () => {
  // 第一步：前端表单前置校验
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 第二步：二次确认，防止误操作
  const confirmText = holidayForm.isHoliday === '1'
    ? `确认设置休假（${holidayForm.holidayStart} 至 ${holidayForm.holidayEnd}）？休假期间菜品将不可购买！`
    : '确认恢复营业？恢复后菜品将重新开放购买！'
  
  try {
    await ElMessageBox.confirm(confirmText, '操作确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: holidayForm.isHoliday === '1' ? 'warning' : 'info'
    })
  } catch (err) {
    ElMessage.info('已取消操作')
    return
  }

  // 第三步：调用后端接口
  loading.value = true
  try {
    // 转换isHoliday为数字，匹配后端接口参数类型
    const submitData = {
      ...holidayForm,
      isHoliday: Number(holidayForm.isHoliday)
    }
    const res = await merchantApi.merchantSetHoliday(submitData)
    ElMessage.success(res.msg || (holidayForm.isHoliday === '1' ? '休假设置成功' : '恢复营业成功'))
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '操作失败，请稍后重试')
    console.error('营业状态设置失败：', error)
  } finally {
    loading.value = false
  }
}

// 8. 表单重置方法：恢复初始状态
const handleReset = () => {
  formRef.value.resetFields()
  // 重置后恢复为当前初始营业状态
  holidayForm.isHoliday = '0'
}
</script>

<style scoped>
/* 页面样式*/
.holiday-setting-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}
.main-card {
  max-width: 800px;
  margin: 0 auto; 
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee; 
}
.holiday-form {
  max-width: 500px; 
}
.el-form-item {
  margin-bottom: 20px; 
}
</style>