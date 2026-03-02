package com.canteen.communitycanteenosystem.service;

import com.canteen.communitycanteenosystem.entity.TDish;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service
 * @Author: 夏日花店
 * @CreateTime: 2026/02/05 23:15
 * @Description: 物品推荐服务接口
 * @Version: 2.0
 */
public interface TRecommendService {

    /**
     * 为用户生成个性化推荐菜品
     */
    List<TDish> getUserRecommend(Long userId, int topN);

    /**
     * 获取指定菜品的搭配推荐
     */
    List<TDish> getRelatedDish(Long dishId, int topN);

    /**
     * 混合推荐
     */
    List<TDish> getHybridRecommend(Long userId, int topN);

    /**
     * 刷新所有矩阵
     */
    void refreshAllMatrix();
}
