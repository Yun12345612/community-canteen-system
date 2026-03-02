package com.canteen.communitycanteenosystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.communitycanteenosystem.entity.TDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 10:52
 * @Description: 菜品Mapper
 * @Version: 1.0
 */
@Mapper
public interface TDishMapper extends BaseMapper<TDish> {

    /**
     * 商家添加菜品（默认上架，status=1）
     */
    int insertDish(TDish dish);

    /**
     * 商家修改菜品（支持价格、库存、封面、描述等字段修改）
     */
    int updateDish(TDish dish);

    /**
     * 商家修改菜品上下架状态（核心操作）
     * @param id 菜品ID
     * @param merchantId 商家ID（做数据隔离）
     * @param status 状态：1=上架，0=下架
     */
    int updateDishStatus(@Param("id") Long id,
                         @Param("merchantId") Long merchantId,
                         @Param("status") Long status);

    /**
     * 商家查询自己的所有菜品（按创建时间倒序）
     */
    List<TDish> selectDishListByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 校验菜品是否存在（用于修改/上下架前的有效性校验）
     */
    int countDishByIdAndMerchantId(@Param("id") Long id,
                                   @Param("merchantId") Long merchantId);

    /**
     * 商家删除菜品（物理删除，数据隔离）
     */
    int deleteDish(@Param("id") Long id,
                   @Param("merchantId") Long merchantId);

}
