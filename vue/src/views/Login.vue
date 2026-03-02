<template>
  <div class="login-page">
    <!-- 登录主容器 -->
    <div class="login-box">
      <div class="login-header">
        <h2 class="title">社区食堂管理系统</h2>
        <p class="desc">商家端 · 高效管理店铺运营</p>
      </div>
      <!-- 登录表单（统一宽度容器，确保表单和按钮对齐） -->
      <div class="form-container">
        <el-form
          :model="loginForm"
          :rules="loginRules"
          ref="formRef"
          class="form"
          label-width="0"
        >
          <el-form-item prop="loginKey">
            <el-input
              v-model="loginForm.loginKey"
              placeholder="账号/手机号/用户名（三选一）"
              size="large"
              prefix-icon="User"
              clearable
              class="form-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入登录密码（6-18位）"
              size="large"
              prefix-icon="Lock"
              clearable
              class="form-input"
            />
          </el-form-item>
        </el-form>
        <!-- 登录按钮 -->
        <el-button
          type="primary"
          size="large"
          @click="handleLogin"
          :loading="loading"
          class="custom-login-btn"
        >
          立即登录
        </el-button>
        <!-- 注册跳转 -->
        <div class="link-area">
          还没有账号？<span @click="toRegister" class="link">立即注册</span>
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
import { merchantLogin } from '@/api/merchant'

// 路由实例（跳转注册页/后台首页）
const router = useRouter()
// 表单Ref + 加载状态
const formRef = ref(null)
const loading = ref(false)

// 登录表单数据（适配后端多方式登录）
const loginForm = reactive({
  loginKey: '', // 接收账号/手机号/用户名
  password: ''
})

// 登录表单校验规则
const loginRules = reactive({
  loginKey: [
    { required: true, message: '请输入账号/手机号/用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' },
    { min: 6, max: 18, message: '密码长度为6-18位', trigger: 'blur' }
  ]
})

// 处理登录逻辑
const handleLogin = async () => {
  // 前端表单前置校验
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // 调用登录接口（GET请求，URL参数）
    const res = await merchantLogin(loginForm)
    ElMessage.success(res.message || '登录成功！') 
    
    // 存储merchantToken到本地缓存（request.js要读取这个键）
    localStorage.setItem('merchantToken', res.data.merchantToken)
    // 存储商家信息（供后台页展示使用）
    localStorage.setItem('merchantInfo', JSON.stringify(res.data))
    
    //跳转路径改为实际商家后台首页（菜品管理页）
    router.push('/home')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败，请检查账号密码或账号状态')
    console.error('登录异常：', error)
  } finally {
    loading.value = false
  }
}

// 跳转到注册页
const toRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
/* 页面全局容器：全屏居中，浅灰背景 */
.login-page {
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

/* 登录盒子：白色背景，圆角阴影，适配小屏幕 */
.login-box {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  padding: 40px 30px;
  transition: all 0.3s ease;
}

/* 头部标题 */
.login-header {
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

/* 表单统一容器：控制表单和按钮的宽度，确保对齐 */
.form-container {
  width: 100%;
  max-width: 300px; /* 与参考图一致的宽度 */
  margin: 0 auto; /* 整体居中 */
}

/* 表单样式 */
.form {
  width: 100%;
}
.el-form-item {
  margin-bottom: 18px;
  width: 100%;
}
/* 输入框样式：和参考图一致的浅蓝底色 */
.form-input {
  --el-input-border-radius: 8px;
  --el-input-height: 44px;
  font-size: 14px;
  background-color: #f0f5ff;
  border-color: #e0ebff;
}

/* 登录按钮：和表单宽度一致，居中对齐 */
.custom-login-btn {
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
.custom-login-btn:hover, .custom-login-btn:focus {
  background-color: #34495e;
  border-color: #34495e;
}
.custom-login-btn:disabled, .custom-login-btn.is-loading {
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
</style>

<style>
/* 全局样式重置：清除默认边距，全屏无滚动条 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
/* 优化Element Plus表单校验提示 */
.el-form-item__error {
  padding: 4px 8px;
  margin-top: 4px;
  font-size: 12px;
}
</style>