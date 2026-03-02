package com.canteen.communitycanteenosystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.communitycanteenosystem.entity.TEvaluation;
import com.canteen.communitycanteenosystem.vo.EvaluationDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 16:49
 * @Description: 评价Mapper（适配现有实体，实现商家查看/回复评价）
 * @Version: 1.0
 */
@Mapper
public interface TEvaluationMapper extends BaseMapper<TEvaluation> {
    /**
     * 商家查询自己的所有评价（数据隔离）
     * @param merchantId 商家ID
     * @return 该商家的所有顾客评价
     */
    List<TEvaluation> listByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 商家回复评价（更新merchantReply和replyTime）
     * @param evaluationId 评价ID
     * @param merchantId 商家ID（防越权）
     * @param merchantReply 回复内容
     * @return 受影响行数
     */
    int replyEvaluation(@Param("evaluationId") Long evaluationId,
                        @Param("merchantId") Long merchantId,
                        @Param("merchantReply") String merchantReply);

    int deleteReply(@Param("evaluationId") Long evaluationId,
                    @Param("merchantId") Long merchantId);

    /**
     * 校验评价是否属于当前商家（防越权）
     * @param evaluationId 评价ID
     * @param merchantId 商家ID
     * @return 1=归属，0=不归属
     */
    int countByEvalIdAndMerchantId(@Param("evaluationId") Long evaluationId,
                                   @Param("merchantId") Long merchantId);

    /**
     * 根据订单ID查询评价
     * @param orderId
     * @return
     */
    TEvaluation selectByOrderId(@Param("orderId") String orderId);

    /**
     * 插入评价
     * @param evaluation
     * @return
     */
    int insert(TEvaluation evaluation);

    /**
     * 根据商家ID查询评价详情
     * @param merchantId
     * @return
     */
    List<EvaluationDetailVO> selectEvaluationDetailByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询用户评价
     * @param userId
     * @return
     */
    List<TEvaluation> listByUserId(@Param("userId") Long userId);
}
