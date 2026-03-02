package com.canteen.communitycanteenosystem.mapper;

import com.canteen.communitycanteenosystem.entity.TDishCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
 * @Author: 夏日花店
 * @CreateTime: 2026/1/29 12:56
 * @Description: 菜品分类Mapper
 * @Version: 1.0
 */
@Mapper
public interface TDishCategoryMapper {

    /**
     * 新增菜品分类
     * @param category 分类实体（含商家ID、分类名称、排序号等）
     * @return 受影响行数（1=成功，0=失败）
     */
    int insertCategory(TDishCategory category);

    /**
     * 修改菜品分类（全字段更新，除主键、创建时间）
     * @param category 分类实体（含主键ID、需修改的字段）
     * @return 受影响行数（1=成功，0=失败）
     */
    int updateCategory(TDishCategory category);

    /**
     * 启用/禁用分类（修改is_valid字段）
     * @param id 分类ID
     * @param isValid 状态：1=启用，0=禁用
     * @param merchantId 商家ID（校验权限，防止修改其他商家分类）
     * @return 受影响行数（1=成功，0=失败/无权限）
     */
    int updateCategoryValid(@Param("id") Long id,
                            @Param("isValid") Long isValid,
                            @Param("merchantId") Long merchantId);

    /**
     * 根据ID查询分类（带商家ID校验，仅查自己的分类）
     * @param id 分类ID
     * @param merchantId 商家ID
     * @return 分类实体/NULL
     */
    TDishCategory selectCategoryById(@Param("id") Long id,
                                     @Param("merchantId") Long merchantId);

    /**
     * 查询商家所有有效分类（is_valid=1），按sort升序排列
     * @param merchantId 商家ID
     * @return 有效分类列表
     */
    List<TDishCategory> selectValidCategoryByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询商家所有分类（含启用/禁用），按sort升序排列
     * @param merchantId 商家ID
     * @return 所有分类列表
     */
    List<TDishCategory> selectAllCategoryByMerchantId(@Param("merchantId") Long merchantId);

}
