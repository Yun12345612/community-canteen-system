package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.entity.TUser;
import com.canteen.communitycanteenosystem.mapper.TUserMapper;
import com.canteen.communitycanteenosystem.service.TUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Timestamp;
import java.time.LocalDateTime;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.controller
 * @Author: 夏日花店
 * @CreateTime: 2026/2/3 17:32
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
public class TUserController {

    @Resource
    private TUserService userService;

    /**
     * 微信小程序登录接口
     *
     * @param code 小程序wx.login()获取的临时code
     * @return 统一响应结果
     */
    @PostMapping("/wechatLogin")
    public Result<?> wechatLogin(@RequestParam String code) {
        try {
            Object result = userService.wechatLogin(code);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/h5Login")
    public Result<?> h5Login(@RequestParam String deviceId) {
        try {
            Object result = userService.h5Login(deviceId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 更新用户信息接口（小程序授权昵称、头像后调用）
     */
    @PostMapping("/updateUserInfo")
    public Result<?> updateUserInfo(@RequestBody TUser user) {
        // 1. 核心校验：主键ID不能为空
        if (user.getId() == null) {
            return Result.fail("用户ID不能为空，无法更新数据");
        }
        // 2. 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        // 3. 使用Service层更新（推荐，支持事务），替代直接调用Mapper
        boolean success = userService.updateById(user);
        // 4. 返回结果
        return success ? Result.success("用户信息更新成功") : Result.fail("用户信息更新失败");
    }
}
