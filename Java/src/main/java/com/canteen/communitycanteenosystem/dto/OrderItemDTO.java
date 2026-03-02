package com.canteen.communitycanteenosystem.dto;

import lombok.Data;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.dto
 * @Author: 夏日花店
 * @CreateTime: 2026/02/07 19:46
 * @Description: 单个菜品下单参数（含备注）
 * @Version: 2.0
 */
@Data
public class OrderItemDTO {
    private Long dishId;            // 菜品ID
    private Integer count;          // 购买数量
    private String remark;          // 菜品个性化备注
}
