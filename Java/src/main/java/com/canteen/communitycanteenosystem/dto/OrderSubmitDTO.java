package com.canteen.communitycanteenosystem.dto;

import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.dto
 * @Author: 夏日花店
 * @CreateTime: 2026/02/07 19:46
 * @Description: 用户下单请求参数DTO
 * @Version: 2.0
 */
@Data
public class OrderSubmitDTO {
    private Long userId;                // 用户ID
    private String receiverName;        // 收货人姓名
    private String receiverPhone;       // 收货人电话
    private String receiverAddress;     // 收货地址
    private String orderRemark;         // 订单整体备注
    private List<OrderItemDTO> dishItems; // 菜品列表
    private Long diningType;           // 就餐方式
}
