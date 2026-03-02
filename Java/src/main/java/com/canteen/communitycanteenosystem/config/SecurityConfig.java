package com.canteen.communitycanteenosystem.config;

import com.canteen.communitycanteenosystem.interceptor.MerchantTokenAuthFilter;
import com.canteen.communitycanteenosystem.interceptor.UserTokenAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security自定义配置类
 * 核心：放行注册/登录接口 + 整合自定义merchantToken认证过滤器
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MerchantTokenAuthFilter merchantTokenAuthFilter;
    @Autowired
    private UserTokenAuthFilter userTokenAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests(auth -> auth
                        // 1. 放行 OPTIONS 预检请求
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 2. 放行商家端所有接口（毕业设计场景，简化认证）
                        .antMatchers("/merchant/**").permitAll()
                        // 3. 放行用户端公开接口
                        .antMatchers("/user/wechatLogin", "/user/h5Login", "/user/evaluation/**", "/user/updateUserInfo").permitAll()
                        // 4. 放行静态资源
                        .antMatchers("/common/**", "/uploads/**").permitAll()
                        // 5. 放行协同推荐接口
                        .antMatchers("/api/recommend/**").permitAll()
                        //6.放行购物车接口
                        .antMatchers("/cart/**").permitAll()
                        // 7.放行订单接口
                        .antMatchers("/user/order/**").permitAll()
                        // 8.放行菜品详情接口
                        .antMatchers("/api/dish/**").permitAll()
                        // 9. 其余请求需认证
                        .anyRequest().authenticated()
                )
                // 过滤器执行顺序：先用户过滤器，再商家过滤器，均在账号密码过滤器之前
                .addFilterBefore(userTokenAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(merchantTokenAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
