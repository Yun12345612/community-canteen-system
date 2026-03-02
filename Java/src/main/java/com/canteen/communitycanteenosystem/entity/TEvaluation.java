package com.canteen.communitycanteenosystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家评价实体类
 */
@Data
public class TEvaluation {
  @TableId(type = IdType.AUTO)
  private Long id; // 评价ID
  private String orderId; // 订单ID
  private Long userId; // 用户ID
  private Long merchantId; // 商家ID
  private Integer  score; // 评分
  private String content; // 评价内容
  private String imgUrls; // 图片链接
  private String merchantReply; // 商家回复
  private LocalDateTime replyTime; // 回复时间
  private LocalDateTime  createTime; // 创建时间
  private LocalDateTime  updateTime; // 更新时间
  private String orderNo; // 订单编号


  @Override
  public String toString() {
    return "TEvaluation{" +
            "id=" + id +
            ", orderId=" + orderId +
            ", userId=" + userId +
            ", merchantId=" + merchantId +
            ", score=" + score +
            ", content='" + content + '\'' +
            ", imgUrls='" + imgUrls + '\'' +
            ", merchantReply='" + merchantReply + '\'' +
            ", replyTime=" + replyTime +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
  }
}