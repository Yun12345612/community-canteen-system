package com.canteen.communitycanteenosystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token 工具类（兼容商家端 + 微信小程序用户端）
 * 适配 jjwt 0.11.5 版本，修复安全编码问题
 */
@Slf4j
@Component
public class JwtUtil {

    // 商家JWT配置
    @Value("${jwt.merchant.secret:your-merchant-secret-key-32bit-12345678}")
    private String merchantSecret;

    @Value("${jwt.user.secret:your-user-secret-key-32bit-abcdefgh}")
    private String userSecret;

    @Value("${jwt.merchant.expire:7200000}")
    private Long merchantExpire;

    @Value("${jwt.user.expire:7200000}")
    private Long userExpire;

    // ====================== 生成秘钥（通用，适配jjwt0.11.5标准写法） ======================
    private SecretKey getSecretKey(String secret) {
        // 密钥长度不足256bit(32字节)会报错，建议配置32位以上随机字符串
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ========== 商家相关方法（原有逻辑，仅重构秘钥生成方式，功能完全不变） ==========
    public String generateMerchantToken(Long merchantId) {
        Date expireDate = new Date(System.currentTimeMillis() + merchantExpire);
        Map<String, Object> claims = new HashMap<>();
        claims.put("merchantId", merchantId);
        claims.put("type", "merchant");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(getSecretKey(merchantSecret))
                .compact();
    }

    public Long getMerchantIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(merchantSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("merchantId", Long.class);
        } catch (Exception e) {
            log.error("解析商家Token失败", e);
            return null;
        }
    }

    public boolean validateMerchantToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(merchantSecret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("商家Token无效: {}", e.getMessage());
            return false;
        }
    }

    // ========== 用户相关方法（适配微信小程序登录，重构后保留原有逻辑） ==========
    public String generateUserToken(Long userId) {
        Date expireDate = new Date(System.currentTimeMillis() + userExpire);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "user");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(getSecretKey(userSecret))
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(userSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("解析用户Token失败", e);
            return null;
        }
    }

    public boolean validateUserToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(userSecret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("用户Token无效: {}", e.getMessage());
            return false;
        }
    }

    // ========== 从Token中获取ID ==========
    public Long getIdFromToken(String token, String type) {
        if ("merchant".equals(type)) {
            return getMerchantIdFromToken(token);
        } else if ("user".equals(type)) {
            return getUserIdFromToken(token);
        }
        return null;
    }
}