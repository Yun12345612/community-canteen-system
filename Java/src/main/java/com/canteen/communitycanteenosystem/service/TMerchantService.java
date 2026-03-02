package com.canteen.communitycanteenosystem.service;

import com.canteen.communitycanteenosystem.entity.TMerchant;

import java.util.Date;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service
 * @Author: 夏日花店
 * @CreateTime: 2026/1/27 15:24
 * @Description: 商家业务
 * @Version: 1.0
 */
public interface TMerchantService {
    /**
     * 商家多方式核心登录业务（支持账号/手机号/用户名任意登录）
     * @param loginKey 登录标识（account/phone/username 任意一个）
     * @param password 登录密码
     * @return 成功返回商家完整信息，失败（标识不存在/密码错误/账号禁用）返回null
     */
    TMerchant login(String loginKey, String password);

    /**
     * 商家注册业务（含参数校验+密码加密+默认状态初始化）
     * @param merchant 商家实体（必传：account/phone/password/merchantName）
     * @return 注册成功返回true，失败（参数为空/账号/手机号重复）返回false
     */
    boolean register(TMerchant merchant);

    /**
     * 商家设置休假/恢复营业
     * @param merchantId 商家ID
     * @param isHoliday 是否休假：0-营业，1-休假
     * @param holidayStart 休假开始时间（休假时必填，营业时可为null）
     * @param holidayEnd 休假结束时间（休假时必填，营业时可为null）
     * @return true-成功，false-失败
     */
    boolean setHolidaySetting(Long merchantId, Integer isHoliday, Date holidayStart, Date holidayEnd);

    /**
     * 校验商家是否处于可营业状态（用于菜品购买前校验）
     * @param merchantId 商家ID
     * @return true-可营业（正常），false-不可营业（休假/账号禁用）
     */
    boolean checkBusinessStatus(Long merchantId);
}
