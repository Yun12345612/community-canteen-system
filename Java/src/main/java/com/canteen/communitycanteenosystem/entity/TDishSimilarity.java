package com.canteen.communitycanteenosystem.entity;

import lombok.Data;

/**
 *  菜品相似度实体类
 */
@Data
public class TDishSimilarity {
    private Long dishId;
    private Long relatedDishId;
    private Double similarity;
}
