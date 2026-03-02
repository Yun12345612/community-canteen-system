package com.canteen.communitycanteenosystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.config
 * @Author: 夏日花店
 * @CreateTime: 2026/02/05 20:59
 * @Description: 默认运营商家ID（对应数据库自增ID）
 * @Version: 2.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "canteen.merchant")
public class MerchantProperties {
    /**
     * 默认运营商家ID（对应数据库自增ID）
     */
    private Long defaultMerchantId;
}
