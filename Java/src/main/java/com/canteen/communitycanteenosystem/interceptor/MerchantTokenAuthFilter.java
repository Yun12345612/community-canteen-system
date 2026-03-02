package com.canteen.communitycanteenosystem.interceptor;

import com.canteen.communitycanteenosystem.config.MerchantProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.canteen.communitycanteenosystem.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
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
 * @CreateTime: 2026/1/29 16:00
 * @Description: 商家Token认证过滤器（Spring Security专用）
 * @Version: 1.0
 */
@Slf4j
@Component
public class MerchantTokenAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MerchantProperties merchantProperties; // 注入默认商家ID配置
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // 白名单接口，支持通配符
    private static final String[] WHITE_LIST = {
            "/merchant/login",
            "/merchant/register",
            "/merchant/dish/list",
            "/merchant/category/list/valid"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // 非商家接口直接放行
        if (!requestUri.startsWith("/merchant/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 通配符匹配白名单
        boolean isWhiteList = false;
        for (String uri : WHITE_LIST) {
            if (antPathMatcher.match(uri, requestUri)) {
                isWhiteList = true;
                break;
            }
        }

        // 白名单接口：设置默认merchantId（和控制器逻辑一致）
        if (isWhiteList) {
            Long merchantId = null;
            String token = request.getHeader("merchantToken");
            if (StringUtils.hasText(token)) {
                try {
                    if (jwtUtil.validateMerchantToken(token)) {
                        merchantId = jwtUtil.getMerchantIdFromToken(token);
                    }
                } catch (Exception e) {
                    merchantId = null;
                }
            }
            if (merchantId == null) {
                merchantId = merchantProperties.getDefaultMerchantId();
            }
            request.setAttribute("merchantId", merchantId);
            filterChain.doFilter(request, response);
            return;
        }

        // 非白名单接口：校验Token
        String token = request.getHeader("merchantToken");
        if (!StringUtils.hasText(token)) {
            request.setAttribute("merchantId", merchantProperties.getDefaultMerchantId());
            filterChain.doFilter(request, response);
            return;
        }

        Long merchantId = null;
        try {
            // 校验Token
            if (jwtUtil.validateMerchantToken(token)) {
                merchantId = jwtUtil.getMerchantIdFromToken(token);
            }
        } catch (Exception e) {
            log.warn("商家Token解析失败：{}", e.getMessage());
            responseError(response, "Token无效或已过期");
            return;
        }

        // 注入认证信息和merchantId
        if (merchantId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    merchantId, null, Collections.emptyList()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            request.setAttribute("merchantId", merchantId); // 设置到请求属性
            log.info("商家认证成功，ID：{}", merchantId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 统一返回JSON格式错误信息
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
