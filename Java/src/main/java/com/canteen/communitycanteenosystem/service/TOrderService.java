package com.canteen.communitycanteenosystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.canteen.communitycanteenosystem.vo.OrderVO;

/**
 * 商家订单管理 Service 接口
 */
public interface TOrderService {

    /**
     * ✅ 唯一查询方法：商家查询订单（支持分页）
     * @param merchantId 商家 ID
     * @param isCompleted true=已完成，false=未完成
     * @param page 当前页码
     * @param pageSize 每页条数
     * @return 分页对象 (包含 records 列表和 total 总数)
     */
    IPage<OrderVO> getMerchantOrderListPage(Long merchantId, Boolean isCompleted, int page, int pageSize);

    boolean completeOrder(String orderId, Long merchantId);

    boolean cancelOrder(String orderId, Long merchantId, String cancelReason);

    boolean recoverOrder(String orderId, Long merchantId);
}