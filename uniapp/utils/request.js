// 基础请求地址
const BASE_URL = 'http://127.0.0.1:8080'
// 超时时间
const TIMEOUT = 10000

/**
 * 统一请求方法（适配小程序用户端 + 商家查询接口 + 推荐接口）
 */
const request = (options) => {
  // 白名单：新增推荐接口 + 原有接口
  const WHITE_LIST = [
    '/user/wechatLogin',
    '/user/h5Login',
    '/merchant/dish/list',
    '/merchant/category/list/valid',
    '/api/recommend/user',    // 新增：用户个性化推荐
    '/api/recommend/related', // 新增：菜品搭配推荐
    '/api/recommend/hybrid'   // 新增：融合算法推荐
  ]
  // 判断当前接口是否需要鉴权
  const needAuth = !WHITE_LIST.some(url => options.url.includes(url))

  // 获取用户Token（与拦截器要求字段一致：token）
  const userToken = uni.getStorageSync('token') || ''

  // 需鉴权但无Token，拦截请求并跳转登录
  if (needAuth && !userToken) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/login/login' })
    }, 1500)
    return Promise.reject(new Error('未登录，终止请求'))
  }

  // 处理URL参数
  let url = `${BASE_URL}${options.url}`
  if (options.params && Object.keys(options.params).length > 0) {
    const paramsArr = []
    Object.keys(options.params).forEach(key => {
      paramsArr.push(`${key}=${encodeURIComponent(options.params[key])}`)
    })
    url += (url.includes('?') ? '&' : '?') + paramsArr.join('&')
  }

  // 请求头配置
  const header = {
    'Content-Type': 'application/json',
    ...options.header,
  }
  // 传递用户Token，字段名严格匹配后端 token
  if (userToken) {
    header.token = userToken
  }

  // 发起请求
  return new Promise((resolve, reject) => {
    uni.request({
      url: url,
      method: options.method || 'GET',
      data: options.data || {},
      header: header,
      timeout: TIMEOUT,
      success: (res) => {
        const { statusCode, data } = res

        // 专属处理401 Token过期/无效
        if (statusCode === 401) {
          // 清除无效Token
          uni.removeStorageSync('token')
          uni.showToast({
            title: data.msg || '登录已过期，请重新登录',
            icon: 'none'
          })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/login/login' })
          }, 1500)
          reject(new Error('Token异常'))
          return
        }

        // 其他HTTP状态码异常
        if (statusCode !== 200) {
          uni.showToast({
            title: `请求异常${statusCode}`,
            icon: 'none'
          })
          reject(res)
          return
        }

        // 核心修复：兼容两种返回格式
        // 格式1：后端返回Result封装（{code:200, data:[]}）
        if (data && typeof data === 'object' && 'code' in data) {
          if (data.code === 200) {
            resolve(data); // 返回完整Result对象
          } else {
            uni.showToast({ title: data.msg || '请求失败', icon: 'none' })
            reject(data);
          }
        } 
        // 格式2：后端直接返回数组/对象（如推荐接口）
        else {
          // 直接resolve原始数据，不校验code
          resolve(data);
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络连接失败，请检查后端服务是否启动',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export default request
