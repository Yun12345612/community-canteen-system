package com.canteen.communitycanteenosystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.communitycanteenosystem.dto.OrderDetailDTO;
import com.canteen.communitycanteenosystem.dto.OrderItemDTO;
import com.canteen.communitycanteenosystem.dto.OrderItemResponseDTO;
import com.canteen.communitycanteenosystem.dto.OrderSubmitDTO;
import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.entity.TOrder;
import com.canteen.communitycanteenosystem.entity.TOrderItem;
import com.canteen.communitycanteenosystem.mapper.TDishMapper;
import com.canteen.communitycanteenosystem.mapper.TOrderItemMapper;
import com.canteen.communitycanteenosystem.mapper.TUserOrderMapper;
import com.canteen.communitycanteenosystem.service.TShoppingCartService;
import com.canteen.communitycanteenosystem.service.TUserOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户订单服务实现类（适配UUID字符串订单ID）
 */
@Service
@Slf4j
public class TUserOrderServiceImpl extends ServiceImpl<TUserOrderMapper, TOrder> implements TUserOrderService {

    @Autowired
    private TOrderItemMapper orderItemMapper;
    @Autowired
    private TDishMapper dishMapper;
    @Autowired
    private TShoppingCartService shoppingCartService;


    /**
     * 提交订单（返回UUID字符串订单ID，解决精度丢失问题）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitDTO submitDTO) {
        try {
            // ===================== 1. 基础参数校验 =====================
            if (submitDTO.getUserId() == null) {
                throw new RuntimeException("用户ID不能为空");
            }
            if (submitDTO.getDishItems() == null || submitDTO.getDishItems().isEmpty()) {
                throw new RuntimeException("菜品列表不能为空");
            }
            if (submitDTO.getReceiverName() == null || submitDTO.getReceiverName().trim().isEmpty()) {
                throw new RuntimeException("收货人姓名不能为空");
            }
            if (submitDTO.getReceiverPhone() == null || submitDTO.getReceiverPhone().trim().isEmpty()) {
                throw new RuntimeException("收货人电话不能为空");
            }
            if (submitDTO.getReceiverAddress() == null || submitDTO.getReceiverAddress().trim().isEmpty()) {
                throw new RuntimeException("收货人地址不能为空");
            }

            // ===================== 2. 菜品校验与金额计算 =====================
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<TOrderItem> orderItemList = new ArrayList<>();
            Long merchantId = null; // 统一商家ID（防止跨商家下单）

            for (OrderItemDTO dishItem : submitDTO.getDishItems()) {
                Long dishId = dishItem.getDishId();
                Integer count = dishItem.getCount();

                // 校验菜品ID和数量
                if (dishId == null) {
                    throw new RuntimeException("菜品ID不能为空");
                }
                if (count == null || count <= 0) {
                    throw new RuntimeException("菜品[" + dishId + "]购买数量必须大于0");
                }

                // 查询菜品信息
                TDish dish = dishMapper.selectById(dishId);
                if (dish == null) {
                    throw new RuntimeException("菜品[" + dishId + "]不存在");
                }
                if (dish.getStatus() == 0) {
                    throw new RuntimeException("菜品[" + dish.getDishName() + "]已下架");
                }

                // 校验商家ID（所有菜品必须属于同一商家）
                if (merchantId == null) {
                    merchantId = dish.getMerchantId();
                } else if (!merchantId.equals(dish.getMerchantId())) {
                    throw new RuntimeException("暂不支持跨商家下单，请分开结算");
                }

                // 校验库存（购物车已扣减，再次确认）
                if (dish.getStock() < count) {
                    throw new RuntimeException("菜品[" + dish.getDishName() + "]库存不足，剩余：" + dish.getStock());
                }

                // 计算小计
                BigDecimal unitPrice = BigDecimal.valueOf(dish.getPrice());
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(count));
                totalAmount = totalAmount.add(subtotal);

                // 构建订单项
                TOrderItem orderItem = new TOrderItem();
                orderItem.setOrderId(""); // 先占位，后续赋值UUID
                orderItem.setDishId(dishId);
                orderItem.setDishName(dish.getDishName());
                orderItem.setUnitPrice(dish.getPrice());
                orderItem.setQuantity(Long.valueOf(count));
                orderItem.setSubtotal(subtotal.doubleValue());
                orderItem.setRemark(dishItem.getRemark());
                orderItem.setCreateTime(LocalDateTime.now());
                orderItemList.add(orderItem);
            }

            // ===================== 3. 创建订单主记录（UUID ID） =====================
            TOrder order = new TOrder();
            // 生成UUID作为订单ID（去掉横线，32位字符串）
            String orderId = UUID.randomUUID().toString().replace("-", "");
            order.setId(orderId); // 赋值UUID字符串ID

            // 订单基础信息
            order.setUserId(submitDTO.getUserId());
            order.setMerchantId(merchantId);
            order.setTotalAmount(totalAmount.doubleValue());

            // 收货人信息
            order.setReceiverName(submitDTO.getReceiverName());
            order.setReceiverPhone(submitDTO.getReceiverPhone());
            order.setReceiverAddress(submitDTO.getReceiverAddress());
            order.setDiningType(submitDTO.getDiningType());

            // 订单编号（业务编号，非主键）
            String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                    + submitDTO.getUserId()
                    + (int) (Math.random() * 1000);
            order.setOrderNo(orderNo);

            // 订单状态
            order.setRemarks(submitDTO.getOrderRemark());
            order.setPayStatus(1L); // 1=待支付
            order.setOrderStatus(1L); // 1=待接单
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());

            // 插入订单主表
            baseMapper.insert(order);
            log.info("订单主记录创建成功，UUID：{}，订单号：{}", orderId, orderNo);

            // ===================== 4. 插入订单项（关联UUID ID） =====================
            for (TOrderItem item : orderItemList) {
                item.setOrderId(orderId); // 赋值UUID订单ID
                orderItemMapper.insert(item);
            }
            log.info("订单{}的订单项插入完成，共{}条菜品", orderId, orderItemList.size());

            // ===================== 5. 清空购物车 =====================
            List<Long> dishIds = submitDTO.getDishItems().stream()
                    .map(OrderItemDTO::getDishId)
                    .collect(Collectors.toList());
            shoppingCartService.clearCartAfterOrder(submitDTO.getUserId(), dishIds);
            log.info("用户{}下单后清空购物车，菜品ID：{}", submitDTO.getUserId(), dishIds);

            // ===================== 6. 返回结果 =====================
            log.info("用户{}下单成功，订单UUID：{}，总金额：{}元",
                    submitDTO.getUserId(), orderId, totalAmount);
            return orderId; // 返回UUID字符串ID

        } catch (Exception e) {
            log.error("用户下单失败", e);
            throw e; // 抛出异常触发事务回滚
        }
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailDTO getOrderDetailWithDishItems(String orderId) {
        // 1. 查询订单主信息
        TOrder order = this.getById(orderId);
        if (order == null) {
            return null;
        }

        // 2. 转换为OrderDetailDTO
        OrderDetailDTO detailDTO = new OrderDetailDTO();
        detailDTO.setId(order.getId());
        detailDTO.setOrderNo(order.getOrderNo());
        detailDTO.setUserId(order.getUserId());
        detailDTO.setTotalAmount(order.getTotalAmount());
        detailDTO.setOrderStatus(order.getOrderStatus().intValue());
        detailDTO.setCreateTime(order.getCreateTime().toString());

        // 3. 查询订单项
        LambdaQueryWrapper<TOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(TOrderItem::getOrderId, orderId);
        List<TOrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

        // 4. 关联查询TDish，补充图片/价格
        List<OrderItemResponseDTO> dishItems = orderItems.stream().map(item -> {
            OrderItemResponseDTO responseDTO = new OrderItemResponseDTO();
            // 从订单项（TOrderItem）取基础信息
            responseDTO.setId(item.getId());
            responseDTO.setDishId(item.getDishId());
            responseDTO.setDishName(item.getDishName());
            responseDTO.setCount(item.getQuantity()); // 数量从TOrderItem的quantity取

            // 关联查询菜品表（TDish），补充图片和价格
            TDish dish = dishMapper.selectById(item.getDishId());
            if (dish != null) { // 防止菜品被删除导致null
                responseDTO.setDishImage(dish.getCoverImg()); // 封面图片
                responseDTO.setUnitPrice(dish.getPrice());     // 菜品价格
            }

            return responseDTO;
        }).collect(Collectors.toList());

        // 5. 设置dishItems（原有逻辑）
        detailDTO.setDishItems(dishItems);
        return detailDTO;
    }
}