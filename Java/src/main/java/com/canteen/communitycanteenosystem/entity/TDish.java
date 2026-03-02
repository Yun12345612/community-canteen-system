package com.canteen.communitycanteenosystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜品实体类
 */
@Data
public class TDish {

  private Long id; // 菜品ID
  private String dishName; // 菜品名称
  private Long categoryId; // 菜品分类ID
  private String categoryName; // 菜品分类名称
  private Double price; // 菜品价格
  private Long stock; // 菜品库存
  private String coverImg; // 菜品封面图片
  private String tasteTag; // 口味标签
  private String description; // 菜品描述
  private Long status; // 菜品状态：1=上架，0=下架
  private LocalDateTime createTime; // 创建时间
  private LocalDateTime updateTime; // 更新时间
  private Long merchantId; // 所属商家ID

  @Override
  public String toString() {
    return "TDish{" +
            "id=" + id +
            ", dishName='" + dishName + '\'' +
            ", categoryId=" + categoryId +
            ", categoryName='" + categoryName + '\'' +
            ", price=" + price +
            ", stock=" + stock +
            ", coverImg='" + coverImg + '\'' +
            ", tasteTag='" + tasteTag + '\'' +
            ", description='" + description + '\'' +
            ", status=" + status +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", merchantId=" + merchantId +
            '}';
  }
}
