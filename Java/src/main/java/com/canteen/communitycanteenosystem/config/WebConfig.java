package com.canteen.communitycanteenosystem.config;

import com.canteen.communitycanteenosystem.interceptor.MerchantTokenAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.config
 * @Author: 夏日花店
 * @CreateTime: 2026/1/29 14:48
 * @Description:  Web 配置类，注册拦截器
 * @Version: 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private MerchantTokenAuthFilter merchantTokenAuthFilter; // 注入过滤器


    @Value("${file.upload.path:uploads/}")
    private String uploadPath;


    /**
     * 注册商家Token过滤器
     */
    @Bean
    public FilterRegistrationBean<MerchantTokenAuthFilter> merchantTokenFilter() {
        FilterRegistrationBean<MerchantTokenAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(merchantTokenAuthFilter);
        registrationBean.addUrlPatterns("/merchant/*"); // 只对商家接口生效
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 优先级最高
        return registrationBean;
    }

    /**
     * 配置静态资源映射，让上传的图片可以通过URL直接访问
     * 例如：http://localhost:8080/uploads/xxx.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /uploads/** 路径到本地上传目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600); // 设置缓存时间（秒），优化性能
    }

    /**
     * 配置跨域，方便小程序调用
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口生效
                .allowedOriginPatterns("*") // 允许所有域名，生产环境请改为具体域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 允许携带cookie
                .maxAge(3600); // 预检请求缓存时间
    }
}