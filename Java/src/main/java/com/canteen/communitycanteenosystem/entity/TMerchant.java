package com.canteen.communitycanteenosystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 商家实体类
 */
@Data
public class TMerchant {
    private Long id;
    private String merchantName; // 食堂名称
    private String account;      // 登录账号（唯一）
    private String username;     // 展示用户名（可重复）
    private String password;     // 加密密码
    private String phone;        // 登录手机号（唯一）
    private String businessHours;// 营业时间
    private Integer  isHoliday;      // 是否休假：0-正常，1-休假
    private Date holidayStart;   // 休假开始时间
    private Date holidayEnd;     // 休假结束时间
    private Timestamp createTime;// 创建时间
    private Timestamp updateTime;// 更新时间
    private Integer  isValid;        // 账号状态：1-启用，0-禁用


    @Override
    public String toString() {
        return "TMerchant{" +
                "id=" + id +
                ", merchantName='" + merchantName + '\'' +
                ", account='" + account + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", businessHours='" + businessHours + '\'' +
                ", isHoliday=" + isHoliday +
                ", holidayStart=" + holidayStart +
                ", holidayEnd=" + holidayEnd +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isValid=" + isValid +
                '}';
    }
}