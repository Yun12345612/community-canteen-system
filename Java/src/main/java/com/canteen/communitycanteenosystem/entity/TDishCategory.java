package com.canteen.communitycanteenosystem.entity;

import lombok.Data;

/**
 * 菜品分类实体类
 */
@Data
public class TDishCategory {

    private Long id; // 分类ID
    private String categoryName; // 分类名称
    private Long sort; // 分类排序
    private java.sql.Timestamp createTime; // 创建时间
    private java.sql.Timestamp updateTime; // 更新时间
    private Long isValid; // 是否有效：1=有效，0=无效
    private Long merchantId; // 所属商家ID

    @Override
    public String toString() {
        return "TDishCategory{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", sort=" + sort +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isValid=" + isValid +
                '}';
    }
}
