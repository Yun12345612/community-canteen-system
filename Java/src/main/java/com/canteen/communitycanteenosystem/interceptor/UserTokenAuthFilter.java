package com.canteen.communitycanteenosystem.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.canteen.communitycanteenosystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.interceptor
 * @Author: 夏日花店
 * @CreateTime: 2026/2/1 1:13
 * @Description: 用户认证过滤器
 * @Version: 1.0
 */
@Component
public class UserTokenAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // 白名单接口
    private static final String[] WHITE_LIST = {
            "/user/wechatLogin",
            "/user/h5Login"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/merchant/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 非用户模块直接放行
        if (!requestUri.startsWith("/user/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 白名单匹配
        for (String uri : WHITE_LIST) {
            if (antPathMatcher.match(uri, requestUri)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            responseError(response, "用户Token不能为空");
            return;
        }

        Long userId = null;
        try {
            if (jwtUtil.validateUserToken(token)) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            System.out.println("用户Token解析失败：" + e.getMessage());
            responseError(response, "Token无效或已过期");
            return;
        }

        // 向SpringSecurity上下文注入认证信息，解决未认证拦截问题
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId, null, Collections.emptyList()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 存入用户ID供Controller使用
        request.setAttribute("userId", userId);
        System.out.println("【DEBUG】设置userId: " + userId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 线程安全：清空上下文
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 统一错误响应
     */
    private void responseError(HttpServletResponse response, String msg) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("msg", msg);
        new ObjectMapper().writeValue(response.getWriter(), result);
    }
}
