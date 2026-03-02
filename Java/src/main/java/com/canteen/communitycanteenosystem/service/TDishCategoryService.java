package com.canteen.communitycanteenosystem.service;

import com.canteen.communitycanteenosystem.entity.TDishCategory;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2026/1/29 12:57
 * @Description: 菜品分类服务接口
 * @Version: 1.0
 */
public interface TDishCategoryService {
    /**
     * 新增菜品分类
     * @param merchantId
     * @param category
     */
    void addCategory(Long merchantId, TDishCategory category);

    /**
     * 修改菜品分类
     * @param merchantId
     * @param category
     */
    void editCategory(Long merchantId, TDishCategory category);

    /**
     * 新增菜品分类
     * @param category 分类实体（含商家ID、分类名称、排序号）
     */
    void addCategory(TDishCategory category);

    /**
     * 修改菜品分类
     * @param category 分类实体（含主键ID、商家ID、需修改字段）
     */
    void editCategory(TDishCategory category);

    /**
     * 启用分类
     * @param id 分类ID
     * @param merchantId 商家ID
     */
    void enableCategory(Long id, Long merchantId);

    /**
     * 禁用分类
     * @param id 分类ID
     * @param merchantId 商家ID
     */
    void disableCategory(Long id, Long merchantId);

    /**
     * 根据ID查询分类（商家专属）
     * @param id 分类ID
     * @param merchantId 商家ID
     * @return 分类实体
     */
    TDishCategory getCategoryById(Long id, Long merchantId);

    /**
     * 查询商家所有有效分类（用于菜品管理的分类下拉框）
     * @param merchantId 商家ID
     * @return 有效分类列表
     */
    List<TDishCategory> getValidCategoryList(Long merchantId);

    /**
     * 查询商家所有分类（含启用/禁用，用于分类管理页面）
     * @param merchantId 商家ID
     * @return 所有分类列表
     */
    List<TDishCategory> getAllCategoryList(Long merchantId);
}
