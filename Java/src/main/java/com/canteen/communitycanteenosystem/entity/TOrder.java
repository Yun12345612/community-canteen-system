package com.canteen.communitycanteenosystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
public class TOrder {

  private String id; // 订单ID
  private String orderNo; // 订单编号
  private Long userId; // 用户ID
  private Double totalAmount; // 订单总金额
  private Long diningType; //  diningType: 1-外带，2-自取
  private String remarks; // 订单备注
  private Long payStatus; // 支付状态：1-待支付，2-支付成功，3-支付失败
  private Long orderStatus; // 订单状态：1-待接单，2-待取餐，3-已完成，4-已取消
  private LocalDateTime payTime; // 支付时间
  private LocalDateTime expectedTime; // 期望取餐时间
  private LocalDateTime actualCompleteTime; // 实际完成时间
  private String cancelReason; // 取消原因
  private LocalDateTime createTime; // 创建时间
  private LocalDateTime updateTime; // 更新时间
  private Long merchantId; // 所属商家ID
  private String receiverName;    // 收货人姓名
  private String receiverPhone;   // 收货人电话
  private String receiverAddress; // 收货地址

  @Override
  public String toString() {
    return "TOrder{" +
            "id=" + id +
            ", orderNo='" + orderNo + '\'' +
            ", userId=" + userId +
            ", totalAmount=" + totalAmount +
            ", diningType=" + diningType +
            ", remarks='" + remarks + '\'' +
            ", payStatus=" + payStatus +
            ", orderStatus=" + orderStatus +
            ", payTime=" + payTime +
            ", expectedTime=" + expectedTime +
            ", actualCompleteTime=" + actualCompleteTime +
            ", cancelReason='" + cancelReason + '\'' +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", merchantId=" + merchantId +
            ", receiverName='" + receiverName + '\'' +
            ", receiverPhone='" + receiverPhone + '\'' +
            ", receiverAddress='" + receiverAddress + '\'' +
            '}';
  }
}
