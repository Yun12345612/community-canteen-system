// src/api/evaluation.js
import request from '../utils/request';

/**
 * 商家评价相关API
 * 适配后端：TMerchantEvaluationController
 */

// 1. 查询商家评价列表
export function getMerchantEvaluationList(merchantId) {
  // 从 localStorage 获取商家ID作为兜底
  if (!merchantId) {
    const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
    merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7; // 默认使用7
  }
  return request({
    url: '/merchant/evaluation/list',
    method: 'get',
    params: { merchantId } // 直接接收外部传入的 merchantId
  });
}

// 2. 商家回复评价
export function replyMerchantEvaluation(params) {
  // 从 localStorage 获取商家ID（Web 环境）
  const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
  const merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7; // 默认使用7
  
  return request({
    url: '/merchant/evaluation/reply',
    method: 'post',
    params: {
      ...params,
      merchantId: Number(merchantId) // 传递商家ID
    }
  });
}

// 3. 获取评价详情（如果需要的话）
export function getEvaluationDetail(evaluationId) {
  const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
  const merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7;
  return request({
    url: '/merchant/evaluation/detail',
    method: 'get',
    params: { evaluationId: String(evaluationId), merchantId: Number(merchantId) }
  });
}

// 4. 删除评价回复（如果需要的话）
export function deleteMerchantReply(evaluationId) {
  const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
  const merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7;
  
  return request({
    url: '/merchant/evaluation/deleteReply',
    method: 'post',
    params: {
      evaluationId: String(evaluationId),
      merchantId: Number(merchantId)
    }
  });
}

export function deleteMerchantEvaluation(evaluationId) {
  const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
  const merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7;
  return request({
    url: '/merchant/evaluation/delete',
    method: 'post',
    params: {
      evaluationId: String(evaluationId),
      merchantId: Number(merchantId)
    }
  });
}

export function updateMerchantEvaluation(data) {
  const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
  const merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7;
  return request({
    url: '/merchant/evaluation/update',
    method: 'post',
    params: { merchantId: Number(merchantId) },
    data
  });
}

export function createMerchantEvaluation(data) {
  const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
  const merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7;
  return request({
    url: '/merchant/evaluation/create',
    method: 'post',
    params: { merchantId: Number(merchantId) },
    data
  });
}

// 5. 获取菜品评价列表
export function getDishEvaluations(evaluationId) {
  return request({
    url: '/merchant/evaluation/dish-evaluations',
    method: 'get',
    params: { 
      evaluationId: String(evaluationId) // 强制转为字符串
    }
  });
}

// 6. 获取商家所有菜品的评价列表（包括直接评价菜品的）
export function getAllDishReviews() {
  const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
  const merchantId = merchantInfo.id || merchantInfo.merchantId || localStorage.getItem('merchantId') || 7;
  return request({
    url: '/merchant/evaluation/all-dish-reviews',
    method: 'get',
    params: { merchantId: Number(merchantId) }
  });
}
