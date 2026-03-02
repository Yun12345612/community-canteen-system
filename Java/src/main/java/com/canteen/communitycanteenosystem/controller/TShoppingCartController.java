package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.entity.TShoppingCart;
import com.canteen.communitycanteenosystem.service.TShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
public class TShoppingCartController {

    @Resource
    private TShoppingCartService shoppingCartService;

    /**
     * 加入购物车（为@RequestBody接收JSON参数）
     */
    @PostMapping("/add")
    public Result<Void> addCart(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        Long dishId = Long.parseLong(params.get("dishId").toString());
        Integer count = params.get("count") != null ? Integer.parseInt(params.get("count").toString()) : 1;

        try {
            shoppingCartService.addCart(userId, dishId, count);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询用户购物车列表（为@RequestBody接收JSON参数）
     */
    @PostMapping("/list") // 建议改为POST（GET传JSON参数不友好）
    public Result<List<TShoppingCart>> getCartList(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        try {
            List<TShoppingCart> cartList = shoppingCartService.getCartList(userId);
            return Result.success(cartList);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量（为@RequestBody接收JSON参数）
     */
    @PutMapping("/update")
    public Result<Void> updateCartCount(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        Long dishId = Long.parseLong(params.get("dishId").toString());
        Integer count = Integer.parseInt(params.get("count").toString());

        try {
            shoppingCartService.updateCartCount(userId, dishId, count);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 清空用户购物车（为@RequestBody接收JSON参数）
     */
    @DeleteMapping("/clear")
    public Result<Void> clearCart(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        try {
            shoppingCartService.clearCart(userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 下单后清空购物车接口（不归还库存）
    @PostMapping("/clearAfterOrder")
    public Result<String> clearCartAfterOrder(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        shoppingCartService.clearCart(userId);
        return Result.success("购物车已清空");
    }
}