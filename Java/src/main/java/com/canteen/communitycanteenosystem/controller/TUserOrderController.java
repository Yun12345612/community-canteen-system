package com.canteen.communitycanteenosystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.dto.OrderDetailDTO;
import com.canteen.communitycanteenosystem.dto.OrderSubmitDTO;
import com.canteen.communitycanteenosystem.entity.TOrder;
import com.canteen.communitycanteenosystem.service.TUserOrderService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 用户订单Controller（适配UUID字符串订单ID）
 */
@RestController
@RequestMapping("/user/order")
@Slf4j
public class TUserOrderController {

    @Autowired
    private TUserOrderService userOrderService;

    /**
     * 用户提交订单接口
     * @param submitDTO 下单参数
     * @return 订单ID（UUID字符串格式）
     */
    @PostMapping("/submit")
    public Result<String> submitOrder(@RequestBody OrderSubmitDTO submitDTO) {
        try {
            // 调用Service层（返回UUID字符串ID）
            String orderId = userOrderService.submitOrder(submitDTO);
            return Result.success("下单成功", orderId);
        } catch (Exception e) {
            log.error("用户下单失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询用户所有订单
     * @param userId 用户ID
     * @return 订单列表（ID为UUID字符串）
     */
    @GetMapping("/list")
    public Result<List<TOrder>> getOrderList(@RequestParam Long userId) {
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        // 按用户ID查询，按创建时间倒序
        LambdaQueryWrapper<TOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TOrder::getUserId, userId)
                .orderByDesc(TOrder::getCreateTime);
        List<TOrder> orderList = userOrderService.list(wrapper);
        log.info("查询用户{}的订单列表，共{}条记录", userId, orderList.size());
        return Result.success(orderList);
    }

    /**
     * 查询单个订单详情（适配UUID字符串ID）
     * @param orderId 订单ID（UUID字符串）
     * @return 订单详情
     */
    @GetMapping("/detail")
    public Result<OrderDetailDTO> getOrderDetail(@RequestParam String orderId) { // 返回值改为OrderDetailDTO
        try {
            // 1. 校验参数
            if (orderId == null || orderId.trim().isEmpty()) {
                return Result.fail("订单ID不能为空");
            }
            // 2. 调用Service层新方法，获取封装好菜品列表的DTO
            OrderDetailDTO orderDetail = userOrderService.getOrderDetailWithDishItems(orderId.trim());
            if (orderDetail == null) {
                log.warn("订单ID{}不存在", orderId);
                return Result.fail("订单不存在");
            }
            return Result.success(orderDetail); // 返回DTO而非TOrder实体
        } catch (Exception e) {
            log.error("查询订单详情失败，订单ID：{}", orderId, e);
            return Result.fail("查询订单失败：" + e.getMessage());
        }
    }

    /**
     * 模拟支付接口（适配UUID字符串订单ID）
     * @param params 支付参数（含orderId）
     * @return 支付结果
     */
    @PostMapping("/mockPay")
    public Map<String, Object> mockPay(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取并校验订单ID
            String orderIdStr = params.get("orderId");
            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                result.put("code", 400);
                result.put("msg", "订单ID不能为空");
                return result;
            }
            String orderId = orderIdStr.trim();

            // 2. 查询订单（校验订单存在且未支付）
            TOrder order = userOrderService.getById(orderId);
            if (order == null) {
                result.put("code", 404);
                result.put("msg", "订单不存在");
                log.warn("支付失败：订单ID{}不存在", orderId);
                return result;
            }
            // 校验订单状态（仅待支付订单可支付）
            if (order.getPayStatus() != 1L) { // 1=待支付
                result.put("code", 400);
                result.put("msg", "订单状态异常，当前支付状态：" + order.getPayStatus());
                log.warn("支付失败：订单ID{}状态异常，支付状态{}", orderId, order.getPayStatus());
                return result;
            }

            // 3. 模拟支付（强制成功，测试用）
            boolean paySuccess = true; // 关闭随机，强制支付成功
            // boolean paySuccess = new Random().nextBoolean(); // 50%成功率

            if (paySuccess) {
                // 4. 支付成功：更新订单状态
                order.setPayStatus(2L); // 2=已支付
                order.setPayTime(LocalDateTime.now()); // 支付时间
                order.setOrderStatus(2L); // 订单状态改为“待出餐”
                order.setUpdateTime(LocalDateTime.now());
                userOrderService.updateById(order);

                result.put("code", 200);
                result.put("msg", "支付成功");
                result.put("status", "success");
                result.put("orderId", orderId);
                result.put("orderNo", order.getOrderNo());
                log.info("订单{}支付成功", orderId);
            } else {
                // 支付失败：仅返回状态，不更新订单
                result.put("code", 500);
                result.put("msg", "支付失败（模拟）");
                result.put("status", "fail");
                log.warn("订单{}支付失败（模拟）", orderId);
            }
        } catch (Exception e) {
            log.error("模拟支付失败", e);
            result.put("code", 500);
            result.put("msg", "支付异常：" + e.getMessage());
        }
        return result;
    }
}