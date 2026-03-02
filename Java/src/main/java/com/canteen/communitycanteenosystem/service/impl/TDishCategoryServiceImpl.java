package com.canteen.communitycanteenosystem.service.impl;

import com.canteen.communitycanteenosystem.entity.TDishCategory;
import com.canteen.communitycanteenosystem.mapper.TDishCategoryMapper;
import com.canteen.communitycanteenosystem.service.TDishCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 菜品分类业务逻辑实现类
 * 提供菜品分类的增删改查、启用禁用等核心功能
 *
 * @author 夏日花店
 * @version 1.0
 * @since 2026-01-29
 */
@Service
public class TDishCategoryServiceImpl implements TDishCategoryService {

    @Autowired
    private TDishCategoryMapper categoryMapper;

    /**
     * 新增菜品分类（商家专用接口）
     * 自动设置商家ID和默认状态
     *
     * @param merchantId 商家ID
     * @param category 分类信息
     * @throws IllegalArgumentException 参数校验失败时抛出
     * @throws RuntimeException 数据库操作失败时抛出
     */
    @Override
    public void addCategory(Long merchantId, TDishCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("分类信息不能为空");
        }
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        category.setMerchantId(merchantId);
        if (category.getIsValid() == null) {
            category.setIsValid(1L);
        }
        if (category.getSort() == null) {
            category.setSort(0L);
        }
        int rows = categoryMapper.insertCategory(category);
        if (rows <= 0) {
            throw new RuntimeException("新增分类失败，请重试");
        }
    }

    /**
     * 修改菜品分类（商家专用接口）
     * 验证分类归属权限后进行更新
     *
     * @param merchantId 商家ID
     * @param category 分类信息（必须包含ID）
     * @throws IllegalArgumentException 参数校验失败时抛出
     * @throws RuntimeException 数据库操作失败或权限不足时抛出
     */
    @Override
    public void editCategory(Long merchantId, TDishCategory category) {
        if (category == null || category.getId() == null || category.getId() <= 0) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        category.setMerchantId(merchantId);

        TDishCategory existCategory = categoryMapper.selectCategoryById(category.getId(), category.getMerchantId());
        if (existCategory == null) {
            throw new RuntimeException("该分类不存在或无操作权限");
        }
        int rows = categoryMapper.updateCategory(category);
        if (rows <= 0) {
            throw new RuntimeException("修改分类失败，请重试");
        }
    }

    /**
     * 新增菜品分类（通用接口）
     * 需要手动指定商家ID
     *
     * @param category 分类信息（必须包含商家ID）
     * @throws IllegalArgumentException 参数校验失败时抛出
     * @throws RuntimeException 数据库操作失败时抛出
     */
    @Override
    public void addCategory(TDishCategory category) {
        // 1. 参数校验
        if (category == null) {
            throw new IllegalArgumentException("分类信息不能为空");
        }
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (category.getMerchantId() == null || category.getMerchantId() <= 0) {
            throw new IllegalArgumentException("商家ID不能为空");
        }
        // 2. 设置默认值：启用状态（1）、排序号默认0
        if (category.getIsValid() == null) {
            category.setIsValid(1L);
        }
        if (category.getSort() == null) {
            category.setSort(0L);
        }
        // 3. 调用Mapper新增
        int rows = categoryMapper.insertCategory(category);
        if (rows <= 0) {
            throw new RuntimeException("新增分类失败，请重试");
        }
    }

    /**
     * 修改菜品分类（通用接口）
     * 验证分类归属权限后进行更新
     *
     * @param category 分类信息（必须包含ID和商家ID）
     * @throws IllegalArgumentException 参数校验失败时抛出
     * @throws RuntimeException 数据库操作失败或权限不足时抛出
     */
    @Override
    public void editCategory(TDishCategory category) {
        // 1. 参数校验
        if (category == null || category.getId() == null || category.getId() <= 0) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (category.getMerchantId() == null || category.getMerchantId() <= 0) {
            throw new IllegalArgumentException("商家ID不能为空");
        }
        // 2. 校验分类是否存在（且属于当前商家）
        TDishCategory existCategory = categoryMapper.selectCategoryById(category.getId(), category.getMerchantId());
        if (existCategory == null) {
            throw new RuntimeException("该分类不存在或无操作权限");
        }
        // 3. 调用Mapper修改
        int rows = categoryMapper.updateCategory(category);
        if (rows <= 0) {
            throw new RuntimeException("修改分类失败，请重试");
        }
    }

    /**
     * 启用菜品分类
     * 将分类状态设置为启用（isValid=1）
     *
     * @param id 分类ID
     * @param merchantId 商家ID（用于权限验证）
     * @throws IllegalArgumentException 参数校验失败时抛出
     * @throws RuntimeException 操作失败或权限不足时抛出
     */
    @Override
    public void enableCategory(Long id, Long merchantId) {
        // 1. 参数校验
        if (id == null || id <= 0 || merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("参数错误，ID不能为空");
        }
        // 2. 调用Mapper启用（isValid=1）
        int rows = categoryMapper.updateCategoryValid(id, 1L, merchantId);
        if (rows <= 0) {
            throw new RuntimeException("启用分类失败，分类不存在或无操作权限");
        }
    }

    /**
     * 禁用菜品分类
     * 将分类状态设置为禁用（isValid=0）
     *
     * @param id 分类ID
     * @param merchantId 商家ID（用于权限验证）
     * @throws IllegalArgumentException 参数校验失败时抛出
     * @throws RuntimeException 操作失败或权限不足时抛出
     */
    @Override
    public void disableCategory(Long id, Long merchantId) {
        // 1. 参数校验
        if (id == null || id <= 0 || merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("参数错误，ID不能为空");
        }
        // 2. 调用Mapper禁用（isValid=0）
        int rows = categoryMapper.updateCategoryValid(id, 0L, merchantId);
        if (rows <= 0) {
            throw new RuntimeException("禁用分类失败，分类不存在或无操作权限");
        }
    }

    /**
     * 根据ID查询菜品分类详情
     *
     * @param id 分类ID
     * @param merchantId 商家ID（用于权限验证）
     * @return 分类信息，不存在时返回null
     * @throws IllegalArgumentException 参数校验失败时抛出
     */
    @Override
    public TDishCategory getCategoryById(Long id, Long merchantId) {
        // 参数校验
        if (id == null || id <= 0 || merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("参数错误，ID不能为空");
        }
        return categoryMapper.selectCategoryById(id, merchantId);
    }

    /**
     * 查询商家下所有有效的菜品分类
     * 只返回启用状态（isValid=1）的分类
     *
     * @param merchantId 商家ID
     * @return 有效分类列表
     * @throws IllegalArgumentException 商家ID无效时抛出
     */
    @Override
    public List<TDishCategory> getValidCategoryList(Long merchantId) {
        // 参数校验
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家ID不能为空");
        }
        return categoryMapper.selectValidCategoryByMerchantId(merchantId);
    }

    /**
     * 查询商家下所有菜品分类
     * 包含启用和禁用的所有分类
     *
     * @param merchantId 商家ID
     * @return 全部分类列表
     * @throws IllegalArgumentException 商家ID无效时抛出
     */
    @Override
    public List<TDishCategory> getAllCategoryList(Long merchantId) {
        // 参数校验
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家ID不能为空");
        }
        return categoryMapper.selectAllCategoryByMerchantId(merchantId);
    }
}

