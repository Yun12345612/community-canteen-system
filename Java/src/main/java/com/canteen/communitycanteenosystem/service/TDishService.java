package com.canteen.communitycanteenosystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.communitycanteenosystem.entity.TDish;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 11:05
 * @Description: 菜品业务
 * @Version: 1.0
 */
public interface TDishService extends IService<TDish> {

    /**
     * 添加菜品
     * @param merchantId
     * @param dish
     * @return
     */
    boolean addDish(Long merchantId, TDish dish);

    /**
     * 修改菜品
     * @param merchantId
     * @param dish
     * @return
     */
    boolean editDish(Long merchantId, TDish dish);

    /**
     * 商家添加菜品（默认上架，status=1）
     * @param dish 菜品信息（需手动设置merchantId）
     * @return 成功返回true，失败false
     */
    boolean addDish(TDish dish);

    /**
     * 商家修改菜品信息
     * @param dish 菜品信息（需包含id、merchantId）
     * @return 成功返回true，失败false
     */
    boolean editDish(TDish dish);

    /**
     * 商家下架菜品（status=0）
     * @param dishId 菜品ID
     * @param merchantId 商家ID
     * @return 成功返回true，失败false
     */
    boolean takeDownDish(Long dishId, Long merchantId);

    /**
     * 商家上架菜品（status=1）
     * @param dishId 菜品ID
     * @param merchantId 商家ID
     * @return 成功返回true，失败false
     */
    boolean putOnDish(Long dishId, Long merchantId);

    /**
     * 商家查询自己的所有菜品
     * @param merchantId 商家ID
     * @return 菜品列表
     */
    List<TDish> getMyDishList(Long merchantId);

    /**
     * 商家删除菜品
     * @param dishId 菜品ID
     * @param merchantId 商家ID
     * @return 成功返回true，失败false
     */
    boolean deleteDish(Long dishId, Long merchantId);

    /**
     * 用户端：查询所有上架的菜品（不限制商家，默认取配置文件默认商家）
     */
    List<TDish> getOnSaleDishList();

    /**
     * 用户端：根据菜品ID查询详情（不限制商家）
     */
    TDish getDishDetailById(Long dishId);
}
