package com.canteen.communitycanteenosystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.communitycanteenosystem.entity.TOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 16:03
 * @Description: 订单明细表Mapper（无外键，纯数据操作）
 * @Version: 1.0
 */
@Mapper
public interface TOrderItemMapper extends BaseMapper<TOrderItem> {
    /**
     * 根据订单ID查询菜品明细
     * @param orderId 订单主表ID
     * @return 该订单的所有菜品明细
     */
    List<TOrderItem> listByOrderId(@Param("orderId") String orderId);
}
