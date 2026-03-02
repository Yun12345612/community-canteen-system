package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.config.MerchantProperties;
import com.canteen.communitycanteenosystem.entity.TDishCategory;
import com.canteen.communitycanteenosystem.service.TDishCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.controller
 * @Author: 夏日花店
 * @CreateTime: 2026/1/27 15:27
 * @Description: 菜品分类控制层
 * @Version: 2.0
 */
@RestController
@RequestMapping("/merchant/category")
public class TMerchantDishCategoryController {

    @Autowired
    private TDishCategoryService categoryService;
    @Autowired
    private MerchantProperties merchantConfig;

    // 新增分类：移除实体中merchantId，从上下文获取后透传
    @PostMapping("/add")
    public Result<Void> addCategory(@RequestAttribute Long merchantId,
                                    @RequestBody TDishCategory category) {
        try {
            categoryService.addCategory(merchantId, category);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    // 修改分类：移除实体中merchantId，从上下文获取后透传
    @PostMapping("/edit")
    public Result<Void> editCategory(@RequestAttribute Long merchantId,
                                     @RequestBody TDishCategory category) {
        try {
            categoryService.editCategory(merchantId, category);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    // 启用分类：从上下文获取
    @PostMapping("/enable")
    public Result<Void> enableCategory(@RequestAttribute Long merchantId,
                                       @RequestParam Long id) {
        try {
            categoryService.enableCategory(id, merchantId);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    // 禁用分类：从上下文获取
    @PostMapping("/disable")
    public Result<Void> disableCategory(@RequestAttribute Long merchantId,
                                        @RequestParam Long id) {
        try {
            categoryService.disableCategory(id, merchantId);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    // 根据ID查询：从上下文获取
    @GetMapping("/get/{id}")
    public Result<TDishCategory> getCategory(@RequestAttribute Long merchantId,
                                             @PathVariable Long id) {
        try {
            TDishCategory category = categoryService.getCategoryById(id, merchantId);
            return Result.success(category);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    // 查询有效分类：从上下文获取
//    @GetMapping("/list/valid")
//    public Result<List<TDishCategory>> getValidCategoryList(@RequestAttribute(required = false) Long merchantId) {
//        // 兼容逻辑：空值赋默认商家ID
//        if(merchantId == null){
//            merchantId = 7L;
//        }
//        try {
//            List<TDishCategory> list = categoryService.getValidCategoryList(merchantId);
//            return Result.success(list);
//        } catch (IllegalArgumentException e) {
//            return Result.fail(e.getMessage());
//        } catch (Exception e) {
//            return Result.fail(500, e.getMessage());
//        }
//    }
    // 查询有效分类
    @GetMapping("/list/valid")
    public Result<List<TDishCategory>> getValidCategoryList(@RequestAttribute(required = false) Long merchantId) {
        // 从配置类读取默认商家ID，不硬编码
        if (merchantId == null) {
            merchantId = merchantConfig.getDefaultMerchantId() ;
        }
        try {
            List<TDishCategory> list = categoryService.getValidCategoryList(merchantId);
            return Result.success(list);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    // 查询所有分类：从上下文获取
    @GetMapping("/list/all")
    public Result<List<TDishCategory>> getAllCategoryList(@RequestAttribute Long merchantId) {
        try {
            List<TDishCategory> list = categoryService.getAllCategoryList(merchantId);
            return Result.success(list);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }
}