package com.canteen.communitycanteenosystem.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.vo
 * @Author: 夏日花店
 * @CreateTime: 2026/1/31 23:00
 * @Description: 菜品评价VO
 * @Version: 1.0
 */


@Data
public class DishEvaluationVO {
    private Long id;           // 菜品评价ID
    private Long evaluationId; // 关联的整体评价ID
    private Long orderItemId;  // 订单项ID（用于匹配回显）
    private Long dishId;       // 菜品ID
    private String dishName;   // 菜品名称
    private Integer score;     // 评分（1-5）
    private String content;    // 评价内容
    private LocalDateTime createTime;   // 创建时间

    // 用户信息（直接评价菜品时使用）
    private Long userId;
    private String userName;
    private String userAvatar;
}
