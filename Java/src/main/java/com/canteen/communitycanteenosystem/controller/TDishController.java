package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.config.MerchantProperties;
import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.service.TDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.controller
 * @Author: 夏日花店
 * @CreateTime: 2026/1/27 15:27
 * @Description: 菜品控制层
 * @Version: 2.0
 */
@RestController
@RequestMapping("/merchant/dish")
public class TDishController {

    @Autowired
    private TDishService dishService;
    @Autowired
    private MerchantProperties merchantProperties;

    // 新增菜品：加required=false + 默认值
    @PostMapping("/add")
    public Result<String> addDish(@RequestAttribute(required = false) Long merchantId,
                                  @RequestBody TDish dish) {
        // 兜底默认值
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = dishService.addDish(merchantId, dish);
        if (success) {
            return Result.success("菜品添加成功，默认上架");
        }
        return Result.fail("菜品添加失败，参数不合法");
    }

    // 修改菜品：加required=false + 默认值
    @PostMapping("/edit")
    public Result<String> editDish(@RequestAttribute(required = false) Long merchantId,
                                   @RequestBody TDish dish) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = dishService.editDish(merchantId, dish);
        if (success) {
            return Result.success("菜品修改成功");
        }
        return Result.fail("菜品修改失败，菜品不存在或参数不合法");
    }

    // 菜品下架：加required=false + 默认值
    @PostMapping("/takeDown")
    public Result<String> takeDownDish(@RequestAttribute(required = false) Long merchantId,
                                       @RequestParam Long dishId) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = dishService.takeDownDish(dishId, merchantId);
        if (success) {
            return Result.success("菜品下架成功");
        }
        return Result.fail("菜品下架失败，菜品不存在或非当前商家菜品");
    }

    // 菜品上架：加required=false + 默认值
    @PostMapping("/putOn")
    public Result<String> putOnDish(@RequestAttribute(required = false) Long merchantId,
                                    @RequestParam Long dishId) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = dishService.putOnDish(dishId, merchantId);
        if (success) {
            return Result.success("菜品上架成功");
        }
        return Result.fail("菜品上架失败，菜品不存在或非当前商家菜品");
    }

    // 原有list接口逻辑不变
    @GetMapping("/list")
    public Result<List<TDish>> getMyDishList(@RequestAttribute(required = false) Long merchantId) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        List<TDish> dishList = dishService.getMyDishList(merchantId);
        if (dishList != null) {
            dishList = dishList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Result.success(dishList != null && !dishList.isEmpty() ? "查询成功" : "暂无菜品", dishList);
    }

    @PostMapping("/delete")
    public Result<String> deleteDish(@RequestAttribute(required = false) Long merchantId,
                                     @RequestParam Long dishId) {
        if (merchantId == null) {
            merchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = dishService.deleteDish(dishId, merchantId);
        return success ? Result.success("删除成功") : Result.fail("删除失败，菜品不存在或无权限");
    }

    @GetMapping("/batch")
    public Result<List<TDish>> getDishByIds(@RequestParam("ids") List<Long> ids) {
        List<TDish> dishList = dishService.listByIds(ids);
        return Result.success(dishList);
    }
}
