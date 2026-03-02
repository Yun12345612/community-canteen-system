package com.canteen.communitycanteenosystem.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.dto
 * @Author: 夏日花店
 * @CreateTime: 2026/1/31 22:59
 * @Description: 菜品评价DTO
 * @Version: 1.0
 */
@Data
public class DishEvaluationDTO {
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;

    @NotNull(message = "订单项ID不能为空")
    private Long orderItemId;

    @NotNull(message = "请给菜品评分")
    @Min(value = 1, message = "评分不能低于1分")
    @Max(value = 5, message = "评分不能高于5分")
    private Integer score;

    private String content;
}
