package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.service.TDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 用户端菜品接口（小程序前端调用）
 */
@RestController
@RequestMapping("/api/dish") // 前端调用的基础路径
@RequiredArgsConstructor
public class DishController {

    private final TDishService dishService;

    /**
     * 用户端：菜品列表接口（前端点餐页/首页调用）
     * 接口地址：http://localhost:8080/api/dish/list
     */
    @GetMapping("/list")
    public Result<List<TDish>> getDishList() {
        List<TDish> dishList = dishService.getOnSaleDishList();
        return Result.success("查询成功", dishList);
    }

    /**
     * 用户端：菜品详情接口（前端详情页调用）
     * 接口地址：http://localhost:8080/api/dish/detail?dishId=1
     */
    @GetMapping("/detail")
    public Result<TDish> getDishDetail(@RequestParam Long dishId) {
        TDish dish = dishService.getDishDetailById(dishId);
        if (dish == null) {
            return Result.fail("菜品不存在");
        }
        return Result.success("查询成功", dish);
    }
}