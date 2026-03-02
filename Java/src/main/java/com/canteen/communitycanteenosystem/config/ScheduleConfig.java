package com.canteen.communitycanteenosystem.config;

import com.canteen.communitycanteenosystem.service.impl.TRecommendServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {
    private final TRecommendServiceImpl recommendService;

    /**
     * 每日凌晨2点刷新双算法矩阵
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshSimilarity() {
        recommendService.refreshAllMatrix();
    }
}