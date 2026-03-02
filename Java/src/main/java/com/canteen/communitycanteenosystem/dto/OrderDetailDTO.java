package com.canteen.communitycanteenosystem.dto;

import lombok.Data;
import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.dto
 * @Author: 夏日花店
 * @CreateTime: 2026/02/08 16:00
 * @Description: 订单详情返回DTO（给前端）
 * @Version: 2.0
 */
@Data
public class OrderDetailDTO {
    // 订单主信息
    private String id;             // 订单ID
    private String orderNo;        // 订单编号
    private Long userId;           // 用户ID
    private Double totalAmount;    // 订单总金额
    private Integer orderStatus;   // 订单状态
    private String createTime;     // 创建时间

    private List<OrderItemResponseDTO> dishItems;
}