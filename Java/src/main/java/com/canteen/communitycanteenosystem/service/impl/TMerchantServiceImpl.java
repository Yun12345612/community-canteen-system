package com.canteen.communitycanteenosystem.service.impl;

import com.canteen.communitycanteenosystem.entity.TMerchant;
import com.canteen.communitycanteenosystem.mapper.TMerchantMapper;
import com.canteen.communitycanteenosystem.service.TMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2026/1/27 15:24
 * @Description: 商家多方式登录业务实现类
 * @Version: 1.0
 */
@Service
public class TMerchantServiceImpl implements TMerchantService {

    // 1.注入Mapper层接口
    @Autowired
    private TMerchantMapper tMerchantMapper;

    // 2.注入BCrypt密码加密器（SpringBoot自带，需在启动类配置Bean）
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 多方式登录核心逻辑：多标识查询 + 加密密码校验
     */
    @Override
    public TMerchant login(String loginKey, String password) {
        // 1. 前置参数非空校验，过滤无效请求
        if (!StringUtils.hasText(loginKey) || !StringUtils.hasText(password)) {
            return null;
        }
        // 2. 调用Mapper多标识查询：匹配account/phone/username，仅返回有效账号（is_valid=1）
        TMerchant merchant = tMerchantMapper.selectMerchantByLoginKey(loginKey.trim());
        // 3. 密码校验：商家存在 + BCrypt加密密码匹配（密文对比，非明文）
        if (merchant != null && passwordEncoder.matches(password, merchant.getPassword())) {
            return merchant;
        }
        // 标识不存在/密码错误/账号禁用，返回null
        return null;
    }

    /**
     * 注册核心逻辑：参数校验 → 密码加密 → 状态初始化 → 调用Mapper插入
     */
    @Override
    public boolean register(TMerchant merchant) {
        // 1. 核心参数非空校验（必传：账号/手机号/密码/食堂名称）
        if (merchant == null
                || !StringUtils.hasText(merchant.getAccount())
                || !StringUtils.hasText(merchant.getPhone())
                || !StringUtils.hasText(merchant.getPassword())
                || !StringUtils.hasText(merchant.getMerchantName())) {
            return false;
        }
        // 2. 密码BCrypt加密（不可逆，密文存储，提升安全性）
        merchant.setPassword(passwordEncoder.encode(merchant.getPassword().trim()));
        // 3. 初始化默认业务状态（无需前端传参，后端统一控制）
        if (merchant.getIsHoliday() == null) {
            merchant.setIsHoliday(0); // 默认：正常营业（0=否，1=是）
        }
        if (merchant.getIsValid() == null) {
            merchant.setIsValid(1);   // 默认：账号启用（1=有效，0=禁用）
        }
        // 4. 用户名默认填充（前端未传则用食堂名称替代）
        if (!StringUtils.hasText(merchant.getUsername())) {
            merchant.setUsername(merchant.getMerchantName());
        }
        // 5. 调用Mapper插入数据，受影响行数>0则注册成功（数据库唯一索引拦截重复账号/手机号）
        return tMerchantMapper.insertMerchant(merchant) > 0;
    }

    /**
     * 设置商家休假设置
     * @param merchantId 商家ID
     * @param isHoliday 是否休假：0-营业，1-休假
     * @param holidayStart 休假开始时间（休假时必填，营业时可为null）
     * @param holidayEnd 休假结束时间（休假时必填，营业时可为null）
     * @return
     */
    @Override
    public boolean setHolidaySetting(Long merchantId, Integer isHoliday, Date holidayStart, Date holidayEnd) {
        // 1. 基础参数校验
        if (merchantId == null || isHoliday == null || (isHoliday != 0 && isHoliday != 1)) {
            return false;
        }
        // 2. 休假状态校验：设置休假时，开始/结束时间不能为空且开始时间<=结束时间
        if (isHoliday == 1) {
            if (ObjectUtils.isEmpty(holidayStart) || ObjectUtils.isEmpty(holidayEnd) || holidayStart.after(holidayEnd)) {
                return false;
            }
        }
        // 3. 执行更新操作
        return tMerchantMapper.updateHolidaySetting(merchantId, isHoliday, holidayStart, holidayEnd) > 0;
    }

    /**
     *
     * @param merchantId 商家ID
     * @return
     */
    @Override
    public boolean checkBusinessStatus(Long merchantId) {
        // 1. 参数校验
        if (merchantId == null) {
            return false;
        }
        // 2. 查询商家营业状态
        TMerchant merchant = tMerchantMapper.selectBusinessStatusById(merchantId);
        if (merchant == null) {
            return false;
        }
        // 3. 双重校验：账号启用（isValid=1）+ 未休假（isHoliday=0）= 可营业
        return merchant.getIsValid() == 1 && merchant.getIsHoliday() == 0;
    }
}