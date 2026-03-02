package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.BusinessException;
import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.dto.DishReviewDTO;
import com.canteen.communitycanteenosystem.dto.EvaluationSubmitDTO;
import com.canteen.communitycanteenosystem.service.TEvaluationService;
import com.canteen.communitycanteenosystem.vo.DishEvaluationVO;
import com.canteen.communitycanteenosystem.vo.EvaluationDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 顾客评价Controller（适配UUID字符串订单ID）
 */
@Slf4j
@RestController
@RequestMapping("/user/evaluation")
public class TUserEvaluationController {

    @Autowired
    private TEvaluationService evaluationService;

    /**
     * 提交评价（核心接口：支持文字+图片+菜品评分）
     */
    @PostMapping("/submit")
    public Result<String> submitEvaluation(@RequestBody @Valid EvaluationSubmitDTO dto,
                                           @RequestAttribute Long userId) {
        try {
            log.info("用户{}提交订单{}的评价，菜品评价数：{}",
                    userId, dto.getOrderId(),
                    dto.getDishEvaluations() != null ? dto.getDishEvaluations().size() : 0);

            // 从上下文赋值用户ID，防止前端篡改
            dto.setUserId(userId);

            boolean success = evaluationService.submitEvaluation(dto);
            if (success) {
                return Result.success("评价成功");
            }
            return Result.fail("评价失败");
        } catch (BusinessException e) {
            // 捕获业务异常，友好提示
            log.warn("用户{}评价失败：{}", userId, e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            // 捕获系统异常，记录详细日志
            log.error("用户{}提交评价异常，订单ID：{}", userId, dto.getOrderId(), e);
            return Result.fail("系统异常，评价提交失败");
        }
    }

    /**
     * 检查订单是否可以评价（适配UUID字符串订单ID）
     */
    @GetMapping("/can-evaluate/{orderId}")
    public Result<Boolean> canEvaluate(@PathVariable String orderId,
                                       @RequestAttribute Long userId) {
        try {
            log.info("校验用户{}是否可评价订单{}", userId, orderId);
            boolean canEvaluate = evaluationService.canEvaluate(orderId, userId);
            return Result.success(canEvaluate);
        } catch (Exception e) {
            log.error("校验订单可评价状态失败，订单ID：{}，用户ID：{}", orderId, userId, e);
            return Result.fail("校验失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<EvaluationDetailVO>> listUserEvaluations(@RequestAttribute Long userId) {
        try {
            List<EvaluationDetailVO> list = evaluationService.getUserEvaluations(userId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询用户评价列表失败，用户ID：{}", userId, e);
            return Result.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 删除评价（用户可以删除自己的评价，然后重新评价）
     */
    @PostMapping("/delete/{orderId}")
    public Result<String> deleteEvaluation(@PathVariable String orderId,
                                           @RequestAttribute Long userId) {
        try {
            log.info("用户{}删除订单{}的评价", userId, orderId);
            boolean success = evaluationService.deleteUserEvaluation(orderId, userId);
            if (success) {
                return Result.success("删除成功，可以重新评价");
            }
            return Result.fail("删除失败");
        } catch (BusinessException e) {
            log.warn("用户{}删除评价失败：{}", userId, e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户{}删除评价异常，订单ID：{}", userId, orderId, e);
            return Result.fail("系统异常，删除失败");
        }
    }

/**
     * 提交单独的菜品评价（不需要订单整体评价）
     */
    @PostMapping("/submit-dish")
    public Result<String> submitDishEvaluation(@RequestBody @Valid EvaluationSubmitDTO dto,
                                               @RequestAttribute Long userId) {
        try {
            log.info("用户{}提交订单{}的菜品评价", userId, dto.getOrderId());
            log.info("dishEvaluations是否为null: {}", dto.getDishEvaluations() == null);
            if (dto.getDishEvaluations() != null) {
                log.info("菜品评价数量: {}", dto.getDishEvaluations().size());
                dto.getDishEvaluations().forEach(dish ->
                    log.info("菜品评价项: orderItemId={}, dishId={}, score={}",
                        dish.getOrderItemId(), dish.getDishId(), dish.getScore()));
            }

            // 从上下文赋值用户ID
            dto.setUserId(userId);

            // 验证订单是否已完成（只检查状态，不检查是否已评价）
            boolean canSubmitDishEval = evaluationService.canSubmitDishEvaluation(dto.getOrderId(), userId);
            if (!canSubmitDishEval) {
                return Result.fail("该订单不可评价，请确认订单已完成");
            }

            // 设置默认的订单评价（5星，无内容）
            if (dto.getScore() == null) {
                dto.setScore(5);
            }

            boolean success = evaluationService.submitEvaluation(dto);
            if (success) {
                return Result.success("菜品评价成功");
            }
            return Result.fail("菜品评价失败");
        } catch (BusinessException e) {
            log.warn("用户{}提交菜品评价失败：{}", userId, e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户{}提交菜品评价异常，订单ID：{}", userId, dto.getOrderId(), e);
            return Result.fail("系统异常，菜品评价提交失败");
        }
    }

    /**
     * 获取订单的菜品评价列表（用于回显已评价数据）
     */
    @GetMapping("/dish-evaluations/{orderId}")
    public Result<List<DishEvaluationVO>> getDishEvaluationsByOrderId(@PathVariable String orderId,
                                                                       @RequestAttribute Long userId) {
        try {
            log.info("用户{}查询订单{}的菜品评价", userId, orderId);
            List<DishEvaluationVO> dishEvaluations = evaluationService.getDishEvaluationsByOrderId(orderId);
            return Result.success(dishEvaluations);
        } catch (Exception e) {
            log.error("查询订单菜品评价失败，订单ID：{}", orderId, e);
            return Result.fail("查询失败");
        }
    }
}
