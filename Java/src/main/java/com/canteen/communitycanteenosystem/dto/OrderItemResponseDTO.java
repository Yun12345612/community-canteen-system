package com.canteen.communitycanteenosystem.dto;

import lombok.Data;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.dto
 * @Author: 夏日花店
 * @CreateTime: 2026/02/08 16:05
 * @Description: 订单项返回DTO（给前端，精简菜品信息）
 * @Version: 2.0
 */
@Data
public class OrderItemResponseDTO {
    private Long id;          // 订单项ID
    private Long dishId;      // 菜品ID
    private String dishName;  // 菜品名称（从TOrderItem取）
    private String dishImage; // 菜品封面图片（对应TDish的coverImg）
    private Double unitPrice; // 菜品单价（对应TDish的price）
    private Long count;       // 购买数量（从TOrderItem的quantity取）

}