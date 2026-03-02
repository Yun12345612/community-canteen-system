package com.canteen.communitycanteenosystem.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价详情VO（用于商家查看）
 */
@Data
public class EvaluationDetailVO {
    // 评价基本信息：id改为String，适配前端传参
    private String id;
    private String orderId;
    private String orderNo;

    // 用户信息
    private Long userId;
    private String userName;
    private String userAvatar;

    // 菜品评价列表
    private List<DishEvaluationVO> dishEvaluations;

    // 整体信息：imgUrls先存字符串，后续在Service层转List
    private Integer score;        // 整体评分
    private String content;       // 整体评价内容
    private String imgUrls;

    // 商家回复
    private String merchantReply;
    private LocalDateTime replyTime;

    // 时间：改为LocalDateTime，适配数据库
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}