// api/recommend.js
import request from '../utils/request.js'

// 用户个性化推荐
export function getUserRecommend(userId, topN) {
  return request({
    url: '/api/recommend/user',
    method: 'get',
    params: { userId, topN }
  })
}

// 菜品搭配推荐
export function getRelatedRecommend(dishId, topN) {
  return request({
    url: '/api/recommend/related',
    method: 'get',
    params: { dishId, topN }
  })
}

// 融合算法推荐
export function getHybridRecommend(userId, topN) {
  return request({
    url: '/api/recommend/hybrid',
    method: 'get',
    params: { userId, topN }
  })
}