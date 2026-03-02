package com.canteen.communitycanteenosystem.entity;

import lombok.Data;

/**
 * 用户相似度实体类
 */
@Data
public class TUserSimilarity {
    private Long userId;
    private Long similarUserId;
    private Double similarity;
}
