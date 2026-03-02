package com.canteen.communitycanteenosystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.communitycanteenosystem.dto.OrderDetailDTO;
import com.canteen.communitycanteenosystem.dto.OrderSubmitDTO;
import com.canteen.communitycanteenosystem.entity.TOrder;

/**
 * 用户订单Service接口
 */
public interface TUserOrderService extends IService<TOrder> {
    /**
     * 用户提交订单核心方法
     * @param submitDTO 下单参数
     * @return 订单ID
     */
    String  submitOrder(OrderSubmitDTO submitDTO);

    // 查询订单详情（含菜品列表）
    OrderDetailDTO getOrderDetailWithDishItems(String orderId);
}