package com.canteen.communitycanteenosystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.communitycanteenosystem.entity.TOrder;
import com.canteen.communitycanteenosystem.entity.TOrderItem;
import com.canteen.communitycanteenosystem.mapper.TMerchantMapper;
import com.canteen.communitycanteenosystem.mapper.TOrderItemMapper;
import com.canteen.communitycanteenosystem.mapper.TOrderMapper;
import com.canteen.communitycanteenosystem.service.TOrderService;
import com.canteen.communitycanteenosystem.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class TOrderServiceImpl implements TOrderService {

    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private TOrderItemMapper orderItemMapper;
    @Autowired
    private TMerchantMapper merchantMapper;

    @Override
    public IPage<OrderVO> getMerchantOrderListPage(Long merchantId, Boolean isCompleted, int page, int pageSize) {
        log.info("==== 调试：获取商家订单列表 (分页) ====");
        log.info("参数：merchantId={}, isCompleted={}, page={}, pageSize={}", merchantId, isCompleted, page, pageSize);

        if (merchantId == null) {
            return new Page<>(page, pageSize, 0);
        }

        // 校验商家存在
        if (merchantMapper.countMerchantById(merchantId) == 0) {
            log.warn("商家 ID={}不存在", merchantId);
            return new Page<>(page, pageSize, 0);
        }

        // 1. 构建分页对象
        Page<OrderVO> mpPage = new Page<>(page, pageSize);

        // 2. 执行分页查询 (Mapper 接口第一个参数必须是 IPage)
        IPage<OrderVO> resultPage = orderMapper.listMerchantOrdersVO(mpPage, merchantId, isCompleted);

        log.info("查询到的订单数量：{}", resultPage.getRecords().size());
        log.info("总记录数：{}", resultPage.getTotal());

        if (CollectionUtils.isEmpty(resultPage.getRecords())) {
            return resultPage;
        }

        // 3. 循环填充订单明细 (orderItemList)
        for (OrderVO vo : resultPage.getRecords()) {
            if (vo.getOrder() != null && vo.getOrder().getId() != null) {
                String orderId = vo.getOrder().getId();
                List<TOrderItem> itemList = orderItemMapper.listByOrderId(orderId);
                vo.setOrderItemList(itemList);
            }
        }

        return resultPage;
    }

    @Override
    public boolean completeOrder(String orderId, Long merchantId) {
        if (orderId == null || orderId.trim().isEmpty() || merchantId == null) return false;
        if (merchantMapper.countMerchantById(merchantId) == 0) return false;
        if (orderMapper.countOrderByMerchant(orderId, merchantId) == 0) return false;

        TOrder order = orderMapper.selectById(orderId);
        if (order == null) return false;
        Long status = order.getOrderStatus();
        // 状态判断
        if (status == null || (status != 1L && status != 2L)) return false;

        // 传入 int 类型 3
        return orderMapper.updateOrderStatus(orderId, merchantId, 3, null) > 0;
    }

    @Override
    public boolean cancelOrder(String orderId, Long merchantId, String cancelReason) {
        if (orderId == null || orderId.trim().isEmpty() || merchantId == null) return false;
        if (!StringUtils.hasText(cancelReason)) return false;
        if (merchantMapper.countMerchantById(merchantId) == 0) return false;
        if (orderMapper.countOrderByMerchant(orderId, merchantId) == 0) return false;

        TOrder order = orderMapper.selectById(orderId);
        if (order == null) return false;
        Long status = order.getOrderStatus();
        if (status == null || (status != 1L && status != 2L)) return false;

        // 传入 int 类型 4
        return orderMapper.updateOrderStatus(orderId, merchantId, 4, cancelReason) > 0;
    }

    @Override
    public boolean recoverOrder(String orderId, Long merchantId) {
        if (orderId == null || orderId.trim().isEmpty() || merchantId == null) return false;
        if (merchantMapper.countMerchantById(merchantId) == 0) return false;
        if (orderMapper.countOrderByMerchant(orderId, merchantId) == 0) return false;

        TOrder order = orderMapper.selectById(orderId);
        if (order == null) return false;
        Long status = order.getOrderStatus();
        if (status == null || status != 4L) return false;

        // 传入 int 类型 1
        return orderMapper.updateOrderStatus(orderId, merchantId, 1, null) > 0;
    }
}