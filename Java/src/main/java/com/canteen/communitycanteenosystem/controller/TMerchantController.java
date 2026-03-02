package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.dto.HolidaySettingDTO;
import com.canteen.communitycanteenosystem.entity.TMerchant;
import com.canteen.communitycanteenosystem.service.TMerchantService;
import com.canteen.communitycanteenosystem.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.controller
 * @Author: 夏日花店
 * @CreateTime: 2026/1/27 15:27
 * @Description: 商家控制层（登录/注册/营业状态设置）- 适配Token认证体系
 * @Version: 2.0
 */
@Slf4j
@RestController
@RequestMapping("/merchant") // 根路径改为/merchant，适配Security放行规则
public class TMerchantController {
    @Autowired
    private TMerchantService tMerchantService;
    @Autowired
    private JwtUtil jwtUtil; // 注入Token工具类，用于登录生成Token

    // 1. 商家多方式登录（生成Token+统一返回格式，路径：/merchant/login）
    @GetMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam String loginKey,
            @RequestParam String password
    ) {
        log.info("商家登录请求 - 登录标识：{}", loginKey);
        TMerchant merchant = tMerchantService.login(loginKey, password);
        if (merchant != null) {
            // 登录成功：生成Token（核心步骤）
            String merchantToken = jwtUtil.generateMerchantToken(merchant.getId());
            // 封装返回结果：Token+商家基础信息（不返回敏感字段如密码）
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("merchantToken", merchantToken); // 前端存储该Token，后续请求头携带
            resultMap.put("merchantId", merchant.getId());
            resultMap.put("merchantName", merchant.getMerchantName());
            resultMap.put("account", merchant.getAccount());
            log.info("商家登录成功 - 商家ID：{}，生成Token成功", merchant.getId());
            return Result.success("登录成功", resultMap);
        } else {
            log.warn("商家登录失败 - 登录标识：{}，原因：标识不存在/密码错误/账号已禁用", loginKey);
            return Result.fail("标识不存在/密码错误/账号已禁用");
        }
    }

    // 2. 商家注册接口（/merchant/register，与Security放行规则一致）
    @PostMapping("/register")
    public Result<String> register(@RequestBody TMerchant merchant) {
        log.info("商家注册请求 - 账号：{}，手机号：{}", merchant.getAccount(), merchant.getPhone());
        boolean isSuccess = tMerchantService.register(merchant);
        if (isSuccess) {
            log.info("商家注册成功 - 账号：{}", merchant.getAccount());
            return Result.success("注册成功，可直接登录");
        } else {
            log.warn("商家注册失败 - 账号：{}，原因：核心参数为空/账号/手机号已存在", merchant.getAccount());
            return Result.fail("注册失败：核心参数为空/账号/手机号已存在");
        }
    }

    /**
     * 商家设置休假/恢复营业（核心改造：从上下文获取merchantId，移除DTO中的merchantId）
     * 路径：/merchant/business/set-holiday
     * @param isHoliday    是否休假：0-恢复营业，1-设置休假
     * @param holidayStart 休假开始时间（格式：yyyy-MM-dd，休假时必填，非休假时可空）
     * @param holidayEnd   休假结束时间（格式：yyyy-MM-dd，休假时必填，非休假时可空）
     * @return 操作结果
     */
    @PostMapping("/set-holiday")
    // 通过@RequestAttribute从Token拦截器获取merchantId，不接收前端传参
    public Result<String> setHolidaySetting(
            @RequestAttribute Long merchantId,
            @RequestParam Integer isHoliday,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date holidayStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date holidayEnd
    ) {
        log.info("商家休假设置请求 - 商家ID：{}，是否休假：{}，开始时间：{}，结束时间：{}",
                merchantId, isHoliday == 1 ? "是" : "否", holidayStart, holidayEnd);

        // 业务校验：休假时必须传开始/结束时间
        if (isHoliday == 1 && (holidayStart == null || holidayEnd == null)) {
            log.warn("商家休假设置失败 - 商家ID：{}，原因：休假时必须填写开始和结束时间", merchantId);
            return Result.fail("设置失败：休假时必须填写开始和结束时间");
        }
        // 恢复营业时，清空日期参数（避免脏数据）
        if (isHoliday == 0) {
            holidayStart = null;
            holidayEnd = null;
        }

        boolean success = tMerchantService.setHolidaySetting(
                merchantId, // 从Token解析，无需前端传参
                isHoliday,
                holidayStart,
                holidayEnd
        );
        if (success) {
            log.info("商家休假设置成功 - 商家ID：{}，当前状态：{}", merchantId, isHoliday == 1 ? "休假" : "营业");
            return Result.success(isHoliday == 1 ? "休假设置成功" : "恢复营业成功");
        } else {
            log.warn("商家休假设置失败 - 商家ID：{}", merchantId);
            return Result.fail("设置失败：商家ID无效或系统异常");
        }
    }
}