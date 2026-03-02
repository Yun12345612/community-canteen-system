// src/api/dish.js
import request from '@/utils/request'; 

// 1. 菜品添加（默认上架）- POST请求，传请求体data
export function dishAdd(data) {
  return request({
    url: '/merchant/dish/add',
    method: 'post',
    data: data
  });
}

// 2. 菜品信息修改 - POST请求，传请求体data
export function dishEdit(data) {
  return request({
    url: '/merchant/dish/edit',
    method: 'post',
    data: data
  });
}

// 3. 菜品下架 - POST请求，传URL参数params
export function dishTakeDown(params) {
  return request({
    url: '/merchant/dish/takeDown',
    method: 'post',
    params: params
  });
}

// 4. 菜品上架 - POST请求，传URL参数params
export function dishPutOn(params) {
  return request({
    url: '/merchant/dish/putOn',
    method: 'post',
    params: params
  });
}

// 5. 查询当前商家所有菜品 - GET请求，传URL参数params
export function dishGetMyList(params) {
  return request({
    url: '/merchant/dish/list',
    method: 'get',
    params: params
  });
}