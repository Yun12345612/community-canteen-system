package com.canteen.communitycanteenosystem.vo;

import com.canteen.communitycanteenosystem.entity.TOrder;
import com.canteen.communitycanteenosystem.entity.TOrderItem;
import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.vo
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 15:08
 * @Description: 订单 VO（用于前端展示，包含明细）
 * @Version: 1.0
 */
@Data
public class OrderVO {
    private TOrder order; // 订单主信息
    private List<TOrderItem> orderItemList; // 订单明细列表
    private String customerName; // 顾客姓名
    private String customerPhone; // 顾客电话
}