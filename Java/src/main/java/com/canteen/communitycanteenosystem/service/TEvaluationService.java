package com.canteen.communitycanteenosystem.service;

import com.canteen.communitycanteenosystem.dto.EvaluationSubmitDTO;
import com.canteen.communitycanteenosystem.entity.TEvaluation;
import com.canteen.communitycanteenosystem.vo.DishEvaluationVO;
import com.canteen.communitycanteenosystem.vo.EvaluationDetailVO;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 16:50
 * @Description: 商家评价管理Service（查看评价+回复评价）
 * @Version: 1.0
 */
public interface TEvaluationService {

    /**
     * 商家查询自己的所有评价
     * @param merchantId 商家ID
     * @return 评价列表（含已回复/未回复状态）
     */
    List<TEvaluation> getMerchantEvaluationList(Long merchantId);

    /**
     * 商家回复顾客评价
     * @param evaluationId 评价ID
     * @param merchantId 商家ID
     * @param merchantReply 回复内容
     * @return true=成功，false=失败
     */
    boolean replyEvaluation(Long evaluationId, Long merchantId, String merchantReply);

    /**
     * 评价提交
     * @param dto
     * @return
     */
    boolean submitEvaluation(EvaluationSubmitDTO dto);

    /**
     * 顾客查看自己的评价
     * @param userId
     * @return
     */
    List<EvaluationDetailVO> getUserEvaluations(Long userId);

    /**
     * 商家查看评价详情
     * @param merchantId
     * @return
     */
    List<EvaluationDetailVO> getMerchantEvaluationDetail(Long merchantId);

    /**
     * 判断订单是否可评价
     * @param orderId
     * @param userId
     * @return
     */
    boolean canEvaluate(String orderId, Long userId);

    /**
     * 判断订单是否可以提交菜品评价（只检查订单状态，不检查是否已评价）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return true=可以提交菜品评价，false=不可提交
     */
    boolean canSubmitDishEvaluation(String orderId, Long userId);

    /**
     * 获取菜品评价列表
     */
    List<DishEvaluationVO> getDishEvaluations(Long evaluationId);

    /**
     * 验证评价是否属于该商家
     */
    boolean validateEvaluationMerchant(Long evaluationId, Long merchantId);

    /**
     * 获取菜品评价列表
     */
    List<DishEvaluationVO> getDishEvaluations(String evaluationId);

    EvaluationDetailVO getMerchantEvaluationDetailById(Long evaluationId, Long merchantId);

    boolean deleteMerchantEvaluation(Long evaluationId, Long merchantId);

    boolean updateMerchantEvaluation(TEvaluation evaluation, Long merchantId);

    TEvaluation createMerchantEvaluation(TEvaluation evaluation, Long merchantId);

    boolean deleteMerchantReply(Long evaluationId, Long merchantId);

/**
     * 用户删除自己的评价（删除后可以重新评价）
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return true=删除成功，false=删除失败
     */
    boolean deleteUserEvaluation(String orderId, Long userId);

    /**
     * 根据菜品ID获取评价列表（带用户信息）
     * @param dishId 菜品ID
     * @return 评价列表
     */
    List<DishEvaluationVO> getDishReviewsByDishId(Long dishId);

    /**
     * 用户直接评价菜品（无需订单）
     * @param dishId 菜品ID
     * @param userId 用户ID
     * @param score 评分
     * @param content 评价内容
     * @return true=成功，false=失败
     */
    boolean submitDishReview(Long dishId, Long userId, Integer score, String content);

    /**
     * 获取商家所有菜品的评价列表（包括直接评价菜品的）
     * @param merchantId 商家ID
     * @return 评价列表
     */
    List<DishEvaluationVO> getAllDishReviewsByMerchantId(Long merchantId);

    /**
     * 根据订单ID获取菜品评价列表
     * @param orderId 订单ID
     * @return 菜品评价列表
     */
    List<DishEvaluationVO> getDishEvaluationsByOrderId(String orderId);
}
