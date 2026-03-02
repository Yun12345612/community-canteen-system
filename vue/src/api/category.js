// src/api/category.js
import request from '../utils/request';

/**
 * 菜品分类相关API（商家端）
 * 适配后端：MerchantDishCategoryController
 */

// 1. 新增菜品分类
export function categoryAdd(data) {
  return request({
    url: '/merchant/category/add',
    method: 'post',
    data: data // 传JSON体：{merchantId, categoryName, sort, isValid}
  });
}

// 2. 修改菜品分类
export function categoryEdit(data) {
  return request({
    url: '/merchant/category/edit',
    method: 'post',
    data: data // 传JSON体：{id, merchantId, categoryName, sort, isValid}
  });
}

// 3. 启用菜品分类
export function categoryEnable(params) {
  return request({
    url: '/merchant/category/enable',
    method: 'post',
    params: params // 传URL参数：{id, merchantId}
  });
}

// 4. 禁用菜品分类
export function categoryDisable(params) {
  return request({
    url: '/merchant/category/disable',
    method: 'post',
    params: params // 传URL参数：{id, merchantId}
  });
}

// 5. 根据ID查询单个分类
export function categoryGet(params) {
  return request({
    url: '/merchant/category/get/' + params.id, // 路径参数id
    method: 'get',
    params: { merchantId: params.merchantId } // 传URL参数merchantId
  });
}

// 6. 查询商家所有有效分类（用于菜品管理下拉框）
export function categoryGetValidList(params) {
  return request({
    url: '/merchant/category/list/valid',
    method: 'get',
    params: params // 传URL参数：{merchantId}
  });
}

// 7. 查询商家所有分类（含启用/禁用，用于分类管理页面）
export function categoryGetAllList(params) {
  return request({
    url: '/merchant/category/list/all',
    method: 'get',
    params: params // 传URL参数：{merchantId}
  });
}