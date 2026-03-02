package com.canteen.communitycanteenosystem.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.communitycanteenosystem.entity.TUser;
import com.canteen.communitycanteenosystem.mapper.TUserMapper;
import com.canteen.communitycanteenosystem.service.TUserService;
import com.canteen.communitycanteenosystem.util.JwtUtil;
import com.canteen.communitycanteenosystem.util.WechatMiniUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2026/2/3 17:30
 * @Description: 用户登录服务实现类
 * @Version: 1.0
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {
    @Resource
    private TUserMapper userMapper;

    @Resource
    private WechatMiniUtil wechatMiniUtil;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public Object wechatLogin(String code) {
        // 1. 调用微信接口，获取openid
        JSONObject wechatResult = wechatMiniUtil.getSessionKeyAndOpenId(code);
        // 2. 校验微信接口返回结果
        Integer errcode = wechatResult.getInt("errcode");
        if (errcode != null && errcode != 0) {
            throw new RuntimeException("微信登录校验失败：" + wechatResult.getStr("errmsg"));
        }
        String openid = wechatResult.getStr("openid");

        // 3. 根据openid查询用户是否已注册
        LambdaQueryWrapper<TUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TUser::getOpenid, openid).eq(TUser::getIsValid, 1);
        TUser user = userMapper.selectOne(wrapper);

        // 4. 用户不存在：自动注册新用户
        if (user == null) {
            user = new TUser();
            user.setOpenid(openid);
            user.setIsValid(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
        }

        // ====================== 调用工具类生成用户Token ======================
        String token = jwtUtil.generateUserToken(user.getId());

        // 6. 封装返回数据
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userInfo", user);
        return resultMap;
    }

    @Override
    public Object h5Login(String deviceId) {
        if (!StringUtils.hasText(deviceId)) {
            throw new RuntimeException("deviceId不能为空");
        }
        String openid = "h5_" + deviceId.trim();

        LambdaQueryWrapper<TUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TUser::getOpenid, openid).eq(TUser::getIsValid, 1);
        TUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            user = new TUser();
            user.setOpenid(openid);
            user.setIsValid(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
        }

        String token = jwtUtil.generateUserToken(user.getId());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userInfo", user);
        return resultMap;
    }
}
