package com.canteen.communitycanteenosystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.canteen.communitycanteenosystem.entity.TOrder;
import com.canteen.communitycanteenosystem.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 16:00
 * @Description: 订单主表Mapper（适配UUID字符串订单ID）
 * @Version: 1.0
 */
@Mapper
public interface TOrderMapper extends BaseMapper<TOrder> {

    /**
     * 根据ID查询订单（适配UUID字符串ID）
     * @param orderId 订单ID（UUID字符串）
     * @return 订单信息
     */

    TOrder selectById(@Param("orderId") String orderId);

    /**
     * 商家修改订单状态（完成/取消/恢复）
     * @param orderId      订单ID（UUID字符串）
     * @param merchantId   商家ID（数据隔离，防止越权）
     * @param orderStatus  目标状态：3-已完成，4-已取消，1-待接单（恢复订单用）
     * @param cancelReason 取消原因（仅状态为4时传值）
     * @return 受影响行数
     */

    int updateOrderStatus(@Param("orderId") String orderId,
                          @Param("merchantId") Long merchantId,
                          @Param("orderStatus") Integer orderStatus,
                          @Param("cancelReason") String cancelReason);

    /**
     * 校验订单是否归属于当前商家（防止越权操作）
     *
     * @param orderId    订单ID（UUID字符串）
     * @param merchantId 商家ID
     * @return 1=归属，0=不归属
     */

    int countOrderByMerchant(@Param("orderId") String orderId,
                             @Param("merchantId") Long merchantId);


    /**
     * 商家查询订单（支持分类：已完成/未完成）
     * @param merchantId 商家ID
     * @param isCompleted 是否已完成
     * @return 订单VO列表
     */
    IPage<OrderVO> listMerchantOrdersVO(IPage<OrderVO> page,
                                        @Param("merchantId") Long merchantId,
                                        @Param("isCompleted") Boolean isCompleted);
}