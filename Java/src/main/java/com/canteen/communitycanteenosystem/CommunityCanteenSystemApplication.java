package com.canteen.communitycanteenosystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 主程序入口
 */
@SpringBootApplication
@EnableScheduling//开启定时任务功能
@MapperScan("com.canteen.communitycanteenosystem.mapper")
public class CommunityCanteenSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityCanteenSystemApplication.class, args);
    }

    // 配置BCryptPasswordEncoder的Bean，供Service层注入
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
