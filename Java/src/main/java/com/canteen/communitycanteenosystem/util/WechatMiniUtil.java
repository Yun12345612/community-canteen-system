package com.canteen.communitycanteenosystem.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.util
 * @Author: 夏日花店
 * @CreateTime: 2026/2/3 17:27
 * @Description: 头部注释
 * @Version: 1.0
 */
@Component
public class WechatMiniUtil {

    @Value("${wechat.mini.app-id}")
    private String appId;

    @Value("${wechat.mini.app-secret}")
    private String appSecret;

    @Value("${wechat.mini.code2session-url}")
    private String code2SessionUrl;

    /**
     * 根据小程序传递的code获取openid和session_key
     * @param code 小程序wx.login()获取的临时凭证
     * @return {openid, session_key, errcode, errmsg}
     */
    public JSONObject getSessionKeyAndOpenId(String code) {
        // 封装请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("appid", appId);
        params.put("secret", appSecret);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        // 调用微信接口（Hutool工具发送GET请求）
        String result = HttpUtil.get(code2SessionUrl, params);
        return JSONUtil.parseObj(result);
    }
}
