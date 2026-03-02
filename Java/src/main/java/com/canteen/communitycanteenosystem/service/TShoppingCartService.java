package com.canteen.communitycanteenosystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.communitycanteenosystem.entity.TShoppingCart;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service
 * @Author: 夏日花店
 * @CreateTime: 2026/02/06 14:08
 * @Description: 购物车服务接口
 * @Version: 2.0
 */
public interface TShoppingCartService extends IService<TShoppingCart> {
    // 加入购物车
    void addCart(Long userId, Long dishId, Integer count);

    // 查询用户购物车列表
    List<TShoppingCart> getCartList(Long userId);

    // 更新购物车数量
    void updateCartCount(Long userId, Long dishId, Integer count);

    // 清空购物车
    void clearCart(Long userId);

    // 下单后清空购物车（不归还库存）
    void clearCartAfterOrder(Long userId,List<Long> dishIds);

    // 删除购物车项
    void deleteCartItem(Long userId, Long dishId);
}