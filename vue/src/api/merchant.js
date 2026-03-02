// src/api/merchant.js
import request from '../utils/request';

//1. 商家登录接口
export function merchantLogin(params) {
  return request({
    url: '/merchant/login',
    method: 'get',
    params: params
  });
}

//2. 商家注册接口
export function merchantRegister(data) {
  return request({
    url: '/merchant/register',
    method: 'post',
    data: data
  });
}

//3. 设置商家营业状态接口
export function merchantSetHoliday(params) {
  return request({
    url: '/merchant/set-holiday',
    method: 'post',
    params: params
  });
}