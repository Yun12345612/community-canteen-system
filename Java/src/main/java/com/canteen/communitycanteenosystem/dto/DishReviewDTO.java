package com.canteen.communitycanteenosystem.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜品直接评价DTO（用户无需订单即可评价菜品）
 */
@Data
public class DishReviewDTO {
    
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;

    @NotNull(message = "请给菜品评分")
    @Min(value = 1, message = "评分不能低于1分")
    @Max(value = 5, message = "评分不能高于5分")
    private Integer score;

    private String content;  // 评价内容（选填）
}