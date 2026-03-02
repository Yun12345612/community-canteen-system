package com.canteen.communitycanteenosystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.config.MerchantProperties;
import com.canteen.communitycanteenosystem.service.TOrderService;
import com.canteen.communitycanteenosystem.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/merchant/order")
@Slf4j
public class TMerchantOrderController {

    @Autowired
    private TOrderService orderService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MerchantProperties merchantProperties;

    @GetMapping("/list")
    public Result<IPage<OrderVO>> listOrders(
            @RequestAttribute(required = false) Long merchantId,
            @RequestParam(value = "isCompleted", required = false, defaultValue = "0") Integer isCompletedInt,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean isCompleted = (isCompletedInt == 1);

        log.info("商家{}查询订单列表，是否已完成：{}, 页码：{}, 每页条数：{}", merchantId, isCompleted, page, pageSize);

        // ✅ 调用新的分页 Service 方法
        IPage<OrderVO> pageResult = orderService.getMerchantOrderListPage(merchantId, isCompleted, page, pageSize);

        return Result.success("订单查询成功", pageResult);
    }

    @PostMapping("/complete")
    public Result<String> completeOrder(@RequestAttribute(required = false) Long merchantId,
                                        @RequestParam String orderId) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        log.info("商家{}完成订单，订单 ID：{}", merchantId, orderId);
        boolean success = orderService.completeOrder(orderId, merchantId);
        return success ? Result.success("订单已完成") : Result.fail("订单完成失败");
    }

    @PostMapping("/cancel")
    public Result<String> cancelOrder(@RequestAttribute(required = false) Long merchantId,
                                      @RequestParam String orderId,
                                      @RequestParam String cancelReason) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        log.info("商家{}取消订单，订单 ID：{}，原因：{}", merchantId, orderId, cancelReason);
        boolean success = orderService.cancelOrder(orderId, merchantId, cancelReason);
        return success ? Result.success("订单已取消") : Result.fail("订单取消失败");
    }

    @PostMapping("/recover")
    public Result<String> recoverOrder(@RequestAttribute(required = false) Long merchantId,
                                       @RequestParam String orderId) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        log.info("商家{}恢复订单，订单 ID：{}", merchantId, orderId);
        boolean success = orderService.recoverOrder(orderId, merchantId);
        return success ? Result.success("订单恢复成功") : Result.fail("订单恢复失败");
    }
}
