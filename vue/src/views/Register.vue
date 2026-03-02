<template>
  <div class="register-page">
    <!-- 注册主容器：左侧推荐名称 + 右侧表单 -->
    <div class="register-box">
      <!-- 左侧推荐商家名称区（还原参考图深色背景） -->
      <div class="left-area">
        <h3 class="left-title">推荐商家名称</h3>
        <ul class="name-list">
          <li
            v-for="(name, idx) in merchantNames"
            :key="idx"
            @click="fillName(name)"
            class="name-item"
          >
            {{ idx+1 }}. {{ name }}
          </li>
        </ul>
      </div>
      <!-- 右侧注册表单区 -->
      <div class="right-area">
        <div class="register-header">
          <h2 class="title">商家账号注册</h2>
          <p class="desc">填写信息完成注册，即可登录后台管理</p>
        </div>
        <!-- 表单统一容器（确保表单和按钮对齐） -->
        <div class="form-container">
          <el-form
            :model="registerForm"
            :rules="registerRules"
            ref="formRef"
            class="form"
            label-width="0"
          >
            <el-form-item prop="account">
              <el-input
                v-model="registerForm.account"
                placeholder="商家账号（6-16位字母/数字，唯一）"
                size="large"
                prefix-icon="UserFilled"
                clearable
                class="form-input"
              />
            </el-form-item>
            <el-form-item prop="merchantName">
              <el-input
                v-model="registerForm.merchantName"
                placeholder="请输入商家名称（可点击左侧推荐填充）"
                size="large"
                prefix-icon="Shop"
                clearable
                class="form-input"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="商家手机号（11位，唯一）"
                size="large"
                prefix-icon="Phone"
                clearable
                class="form-input"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="设置登录密码（6-18位）"
                size="large"
                prefix-icon="Lock"
                clearable
                class="form-input"
              />
            </el-form-item>
          </el-form>
          <!-- 完成注册按钮 -->
          <el-button
            type="primary"
            size="large"
            @click="handleRegister"
            :loading="loading"
            class="custom-register-btn"
          >
            完成注册
          </el-button>
          <!-- 登录跳转 -->
          <div class="link-area">
            已有账号？<span @click="toLogin" class="link">立即登录</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 核心依赖导入
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
// 导入商家接口
import { merchantRegister } from '@/api/merchant'

// 路由实例（跳转登录页）
const router = useRouter()
// 表单Ref + 加载状态
const formRef = ref(null)
const loading = ref(false)

// 7个社区食堂推荐商家名称（支持点击填充）
const merchantNames = ref([
  "幸福里社区食堂",
  "邻里亲便民食堂",
  "暖心家园社区餐厅",
  "惠民坊社区食堂",
  "家和苑便民餐堂",
  "乐居社区暖心食堂",
  "康馨园邻里食堂"
])

// 注册表单数据：与后端TMerchant实体 一字不差匹配
const registerForm = reactive({
  account: '',        // 对应后端account（唯一登录账号）
  merchantName: '',   // 对应后端merchantName（食堂名称，核心字段）
  phone: '',          // 对应后端phone（唯一手机号）
  password: ''        // 对应后端password（登录密码）
})

// 注册表单校验规则：严格校验格式/长度/必填
const registerRules = reactive({
  account: [
    { required: true, message: '请输入商家账号', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]{6,16}$/, message: '账号为6-16位字母/数字', trigger: 'blur' }
  ],
  merchantName: [
    { required: true, message: '请输入商家名称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置登录密码', trigger: 'blur' },
    { min: 6, max: 18, message: '密码长度为6-18位', trigger: 'blur' }
  ]
})

// 点击左侧推荐名称，填充到商家名称输入框
const fillName = (name) => {
  registerForm.merchantName = name
  ElMessage.info(`已选择商家名称：${name}`)
}

// 处理注册逻辑
const handleRegister = async () => {
  // 前端表单前置校验
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // 调用注册接口（POST请求，JSON请求体，字段与后端完全匹配）
    const res = await merchantRegister(registerForm)
    ElMessage.success(res.msg || '注册成功！请前往登录')
    // 注册成功后自动跳转到登录页
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '注册失败，账号/手机号已存在或参数错误')
    console.error('注册异常：', error)
  } finally {
    loading.value = false
  }
}

// 跳转到登录页
const toLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 页面全局容器：全屏居中，浅灰背景 */
.register-page {
  width: 100vw;
  height: 100vh;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
  margin: 0;
}

/* 注册盒子：左右布局，白色背景，圆角阴影 */
.register-box {
  width: 100%;
  max-width: 800px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  display: flex;
  overflow: hidden;
}

/* 左侧推荐名称区：还原参考图深色背景 */
.left-area {
  flex: 0 0 30%;
  background-color: #2c3e50;
  color: #fff;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
}
.left-title {
  font-size: 18px;
  font-weight: 500;
  text-align: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.2);
}
.name-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
}
.name-item {
  font-size: 14px;
  color: #f0f2f5;
  margin-bottom: 12px;
  padding: 4px 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.name-item:hover {
  color: #fff;
  background-color: rgba(255,255,255,0.1);
}

/* 右侧表单区 */
.right-area {
  flex: 1;
  padding: 40px 30px;
}
.register-header {
  text-align: center;
  margin-bottom: 35px;
}
.title {
  font-size: 24px;
  color: #333;
  font-weight: 600;
  margin-bottom: 8px;
}
.desc {
  font-size: 14px;
  color: #999;
}

/* 表单统一容器：控制宽度+居中 */
.form-container {
  width: 100%;
  max-width: 320px;
  margin: 0 auto;
}
.form {
  width: 100%;
}
.el-form-item {
  margin-bottom: 18px;
  width: 100%;
}
/* 输入框样式：浅蓝底色，统一风格 */
.form-input {
  --el-input-border-radius: 8px;
  --el-input-height: 44px;
  font-size: 14px;
  background-color: #f0f5ff;
  border-color: #e0ebff;
}

/* 完成注册按钮：与表单宽度一致，居中对齐 */
.custom-register-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background-color: #2c3e50;
  border-color: #2c3e50;
  margin: 10px 0 20px 0;
  transition: all 0.2s ease;
}
.custom-register-btn:hover, .custom-register-btn:focus {
  background-color: #34495e;
  border-color: #34495e;
}
.custom-register-btn:disabled, .custom-register-btn.is-loading {
  background-color: #999;
  border-color: #999;
  cursor: not-allowed;
}

/* 跳转链接 */
.link-area {
  text-align: center;
  font-size: 13px;
  color: #999;
}
.link {
  color: #2c3e50;
  cursor: pointer;
  font-weight: 500;
}
.link:hover {
  color: #34495e;
}

/* 自适应样式：小屏幕（768px以下）转为上下布局 */
@media (max-width: 768px) {
  .register-box {
    flex-direction: column;
    height: auto;
  }
  .left-area {
    flex: none;
    padding: 30px 20px;
  }
  .name-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  .name-item {
    margin-bottom: 0;
    padding: 6px 10px;
  }
  .right-area {
    padding: 30px 20px;
  }
}
</style>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body {
  width: 100%;
  height: 100%;
  overflow: auto;
}
/* 优化Element Plus表单校验提示 */
.el-form-item__error {
  padding: 4px 8px;
  margin-top: 4px;
  font-size: 12px;
}
</style>