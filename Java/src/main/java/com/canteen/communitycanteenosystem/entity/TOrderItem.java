package com.canteen.communitycanteenosystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单明细实体类
 */
@Data
public class TOrderItem {

  private Long id; // 订单项ID
  private String orderId; // 订单ID
  private Long dishId; // 菜品ID
  private String dishName; // 菜品名称
  private Double unitPrice; // 单价
  private Long quantity;    // 数量
  private Double subtotal;  // 小计
  private LocalDateTime createTime; // 创建时间
  private String remark;// 备注

  @Override
  public String toString() {
    return "TOrderItem{" +
            "id=" + id +
            ", orderId=" + orderId +
            ", dishId=" + dishId +
            ", dishName='" + dishName + '\'' +
            ", unitPrice=" + unitPrice +
            ", quantity=" + quantity +
            ", subtotal=" + subtotal +
            ", remark='" + remark + '\'' +
            ", createTime=" + createTime +
            '}';
  }
}
