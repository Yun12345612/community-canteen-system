// /api/user.js
import request from '../utils/request.js'

/**
 * 微信登录接口
 * @param {string} code 小程序登录code
 */
export function wechatLogin(code) {
  return request({
    url: '/user/wechatLogin',
    method: 'POST',
    params: { code }
  })
}

/**
 * H5 登录接口（浏览器环境不支持 wx.login）
 * @param {string} deviceId 浏览器侧设备标识
 */
export function h5Login(deviceId) {
  return request({
    url: '/user/h5Login',
    method: 'POST',
    params: { deviceId }
  })
}

/**
 * 更新用户信息接口（核心新增）
 * @param {object} data 用户信息（必传id，可选昵称/签名/头像）
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/updateUserInfo',
    method: 'POST',
    data: data
  })
}
