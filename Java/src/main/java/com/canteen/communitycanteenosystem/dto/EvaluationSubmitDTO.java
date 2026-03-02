package com.canteen.communitycanteenosystem.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 评价提交DTO（适配UUID字符串订单ID）
 */
@Data
public class EvaluationSubmitDTO {
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    // 用户ID由Controller从上下文获取，无需前端传递，保留但不校验
    private Long userId;

    // 整体评价
    private String content;

    // 评分（1-5星，默认5星）
    private Integer score;

    // 评价图片URL列表
    private List<String> imgUrls;

    // 菜品评价列表（可选，订单评价时不需要传）
    private List<DishEvaluationDTO> dishEvaluations;
}