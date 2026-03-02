import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建Axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 5000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 请求拦截器：Token请求头名称
service.interceptors.request.use(
  (config) => {
    // 1. 携带商家ID
    const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}')
    const merchantId = merchantInfo.id
    if (merchantId) {
      config.headers['X-Merchant-Id'] = merchantId
    }

    // merchantToken
    const merchantToken = localStorage.getItem('merchantToken')
    if (merchantToken) {
      config.headers['merchantToken'] = merchantToken
      console.log('请求头已携带商家Token：', merchantToken)
    }

    return config
  },
  (error) => {
    ElMessage.error('请求发送失败，请稍后重试')
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data && typeof data === 'object' && 'code' in data) {
      if (data.code === 200) {
        return data
      }
      const msg = data.msg || data.message || '请求失败'
      ElMessage.error(msg)
      return Promise.reject(Object.assign(new Error(msg), { data }))
    }
    return data
  },
  (error) => {
    const errMsg = error.response?.data?.msg || error.response?.data?.message || '服务器异常，请稍后重试'
    ElMessage.error(errMsg)

    if (error.response?.status === 401) {
      // 清除缓存
      localStorage.removeItem('merchantInfo')
      localStorage.removeItem('merchantToken')
      router.push({ path: '/login' })
      ElMessage.warning('登录状态已过期，请重新登录')
    }
    return Promise.reject(error)
  }
)

export default service
