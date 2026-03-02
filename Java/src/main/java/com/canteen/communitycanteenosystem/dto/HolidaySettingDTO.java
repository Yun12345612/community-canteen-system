package com.canteen.communitycanteenosystem.dto;

import lombok.Data;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.dto
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 17:38
 * @Description: 休假设置
 * @Version: 1.0
 */
@Data
public class HolidaySettingDTO {
    private Long merchantId;
    private Integer isHoliday;
    private String holidayStart;
    private String holidayEnd;
}