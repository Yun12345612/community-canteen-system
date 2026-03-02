package com.canteen.communitycanteenosystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
@TableName("t_shopping_cart")
public class TShoppingCart {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID 关联 t_user.id
     */
    private Long userId;

    /**
     * 菜品ID 关联 t_dish.id
     */
    private Long dishId;

    /**
     * 商家ID 关联 t_dish.merchantId
     */
    private Long merchantId;

    /**
     * 购买数量
     */
    private Integer count;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
    /**
     * 菜品名称
     */
    private String dishName;
    /**
     * 菜品图片
     */
    private String coverImg;
    /**
     * 菜品价格
     */
    private Double price;
}