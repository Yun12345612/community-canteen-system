package com.canteen.communitycanteenosystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.communitycanteenosystem.entity.TUser;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service
 * @Author: 夏日花店
 * @CreateTime: 2026/2/3 17:30
 * @Description: 用户业务接口
 * @Version: 1.0
 */
public interface TUserService extends IService<TUser> {

    /**
     * 微信小程序登录/注册核心方法
     * @param code 小程序临时登录凭证
     * @return 登录成功的用户信息+token
     */
    Object wechatLogin(String code);

    /**
     * H5 登录（浏览器环境不支持 wx.login，提供设备ID登录/注册）
     * @param deviceId 浏览器侧生成的设备标识
     * @return 登录成功的用户信息+token
     */
    Object h5Login(String deviceId);
}
