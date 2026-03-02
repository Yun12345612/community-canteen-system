package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.config.MerchantProperties;
import com.canteen.communitycanteenosystem.entity.TEvaluation;
import com.canteen.communitycanteenosystem.service.TEvaluationService;
import com.canteen.communitycanteenosystem.vo.DishEvaluationVO;
import com.canteen.communitycanteenosystem.vo.EvaluationDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.controller
 * @Author: 夏日花店
 * @CreateTime: 2026/1/27 15:27
 * @Description: 商家评价Controller
 * @Version: 2.0
 */
@Slf4j
@RestController
@RequestMapping("/merchant/evaluation")
public class TMerchantEvaluationController {

    @Autowired
    private TEvaluationService evaluationService;

    @Autowired
    private MerchantProperties merchantProperties;

    // 查询商家评价（带菜品评价详情）
    @GetMapping("/list")
    public Result<List<EvaluationDetailVO>> listEvaluation(@RequestAttribute(required = false) Long merchantId,
                                                           @RequestParam(value = "merchantId", required = false) Long merchantIdParam) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        List<EvaluationDetailVO> evaluationList = evaluationService.getMerchantEvaluationDetail(resolvedMerchantId);
        if (evaluationList == null || evaluationList.isEmpty()) {
            return Result.success("暂无评价", new ArrayList<>());
        }
        return Result.success("评价查询成功", evaluationList);
    }

    // 回复评价
    @PostMapping("/reply")
    public Result<String> replyEvaluation(@RequestAttribute(required = false) Long merchantId,
                                          @RequestParam(value = "merchantId", required = false) Long merchantIdParam,
                                          @RequestParam Long evaluationId,
                                          @RequestParam String merchantReply) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = evaluationService.replyEvaluation(evaluationId, resolvedMerchantId, merchantReply);
        if (success) {
            return Result.success("评价回复成功");
        }
        return Result.fail("回复失败（评价不存在/无权限/参数错误）");
    }


    /**
     * 获取菜品评价详情
     */
    @GetMapping("/dish-evaluations")
    public Result<List<DishEvaluationVO>> getDishEvaluations(@RequestParam Long evaluationId,
                                                             @RequestAttribute(required = false) Long merchantId) {
        try {
            if (merchantId == null) {
                merchantId = merchantProperties.getDefaultMerchantId();
            }
            // 1. 验证权限（调用添加的service方法）
            boolean hasPermission = evaluationService.validateEvaluationMerchant(evaluationId, merchantId);
            if (!hasPermission) {
                return Result.fail("无权限查看此评价");
            }

            // 2. 查询菜品评价（调用添加的service方法）
            List<DishEvaluationVO> dishEvaluations = evaluationService.getDishEvaluations(evaluationId);
            return Result.success("查询成功", dishEvaluations);

        } catch (Exception e) {
            log.error("查询菜品评价失败", e);
            return Result.fail("查询失败");
        }
    }

    @GetMapping("/detail")
    public Result<?> getEvaluationDetail(@RequestParam Long evaluationId,
                                         @RequestAttribute(required = false) Long merchantId,
                                         @RequestParam(value = "merchantId", required = false) Long merchantIdParam) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        EvaluationDetailVO vo = evaluationService.getMerchantEvaluationDetailById(evaluationId, resolvedMerchantId);
        if (vo == null) {
            return Result.fail("评价不存在或无权限");
        }
        return Result.success("查询成功", vo);
    }

    @PostMapping("/deleteReply")
    public Result<String> deleteReply(@RequestParam Long evaluationId,
                                      @RequestAttribute(required = false) Long merchantId,
                                      @RequestParam(value = "merchantId", required = false) Long merchantIdParam) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = evaluationService.deleteMerchantReply(evaluationId, resolvedMerchantId);
        return success ? Result.success("删除回复成功") : Result.fail("删除回复失败（评价不存在/无权限）");
    }

    @PostMapping("/delete")
    public Result<String> deleteEvaluation(@RequestParam Long evaluationId,
                                           @RequestAttribute(required = false) Long merchantId,
                                           @RequestParam(value = "merchantId", required = false) Long merchantIdParam) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = evaluationService.deleteMerchantEvaluation(evaluationId, resolvedMerchantId);
        return success ? Result.success("删除评价成功") : Result.fail("删除评价失败（评价不存在/无权限）");
    }

    @PostMapping("/update")
    public Result<String> updateEvaluation(@RequestBody TEvaluation evaluation,
                                           @RequestAttribute(required = false) Long merchantId,
                                           @RequestParam(value = "merchantId", required = false) Long merchantIdParam) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        boolean success = evaluationService.updateMerchantEvaluation(evaluation, resolvedMerchantId);
        return success ? Result.success("更新评价成功") : Result.fail("更新评价失败（评价不存在/无权限/参数错误）");
    }

@PostMapping("/create")
    public Result<?> createEvaluation(@RequestBody TEvaluation evaluation,
                                      @RequestAttribute(required = false) Long merchantId,
                                      @RequestParam(value = "merchantId", required = false) Long merchantIdParam) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        TEvaluation created = evaluationService.createMerchantEvaluation(evaluation, resolvedMerchantId);
        if (created == null || created.getId() == null) {
            return Result.fail("新增评价失败（参数错误）");
        }
        return Result.success("新增评价成功", created);
    }

    /**
     * 获取商家所有菜品评价列表（包括直接评价菜品的）
     */
    @GetMapping("/all-dish-reviews")
    public Result<List<DishEvaluationVO>> getAllDishReviews(@RequestAttribute(required = false) Long merchantId,
                                                            @RequestParam(value = "merchantId", required = false) Long merchantIdParam) {
        Long resolvedMerchantId = merchantIdParam != null ? merchantIdParam : merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = merchantProperties.getDefaultMerchantId();
        }
        List<DishEvaluationVO> reviews = evaluationService.getAllDishReviewsByMerchantId(resolvedMerchantId);
        return Result.success("查询成功", reviews);
    }
}
