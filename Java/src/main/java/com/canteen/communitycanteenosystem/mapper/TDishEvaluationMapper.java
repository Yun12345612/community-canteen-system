package com.canteen.communitycanteenosystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.communitycanteenosystem.entity.TDishEvaluation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TDishEvaluationMapper extends BaseMapper<TDishEvaluation> {

    /**
     * 批量插入菜品评价
     */
    int batchInsert(@Param("list") List<TDishEvaluation> dishEvaluations);

    /**
     * 根据订单评价ID查询菜品评价（支持Long和String类型）
     */
    List<TDishEvaluation> selectByEvaluationId(@Param("evaluationId") Object evaluationId);

    /**
     * 根据菜品ID统计评价数量
     */
    int countByDishId(@Param("dishId") Long dishId);

/**
     * 根据订单评价ID删除菜品评价
     */
    int deleteByEvaluationId(@Param("evaluationId") String evaluationId);

    /**
     * 根据菜品ID查询评价列表（带用户信息）
     */
    List<TDishEvaluation> selectByDishId(@Param("dishId") Long dishId);

    /**
     * 插入单条菜品评价
     */
    int insertSingle(TDishEvaluation dishEvaluation);

    /**
     * 根据订单ID查询菜品评价（通过evaluation_id关联t_evaluation表）
     */
    List<TDishEvaluation> selectByOrderId(@Param("orderId") String orderId);
}