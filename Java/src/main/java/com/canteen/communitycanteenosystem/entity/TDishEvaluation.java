package com.canteen.communitycanteenosystem.entity;

import lombok.Data;

/**
 * 菜品评价实体类
 * 支持两种评价方式：
 * 1. 订单评价时关联：evaluationId 和 orderItemId 有值
 * 2. 直接评价菜品：userId 有值，evaluationId 和 orderItemId 为空
 */
@Data
public class TDishEvaluation {

  private Long id;
  private String evaluationId;  // 关联的订单评价ID（直接评价时为空）
  private Long orderItemId;     // 订单项ID（直接评价时为空）
  private Long dishId;
  private String dishName;
  private Long userId;          // 用户ID（直接评价时使用）
  private Long score;
  private String content;
  private String merchantReply; // 商家回复内容
  private java.sql.Timestamp replyTime; // 商家回复时间
  private java.sql.Timestamp createTime;
  private java.sql.Timestamp updateTime;

  @Override
  public String toString() {
    return "TDishEvaluation{" +
            "id=" + id +
            ", evaluationId=" + evaluationId +
            ", orderItemId=" + orderItemId +
            ", dishId=" + dishId +
            ", dishName='" + dishName + '\'' +
            ", userId=" + userId +
            ", score=" + score +
            ", content='" + content + '\'' +
            ", merchantReply='" + merchantReply + '\'' +
            ", replyTime=" + replyTime +
            ", createTime=" + createTime +
            '}';
  }
}
