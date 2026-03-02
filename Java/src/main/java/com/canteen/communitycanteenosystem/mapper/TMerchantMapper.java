    package com.canteen.communitycanteenosystem.mapper;

    import com.canteen.communitycanteenosystem.entity.TMerchant;
    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;

    import java.util.Date;

    /**
     * @BelongsProject: community-canteen-system
     * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
     * @Author: 夏日花店
     * @CreateTime: 2026/1/27 15:18
     * @Description: 商家mapper
     * @Version: 1.0
     */
    @Mapper
    public interface TMerchantMapper {
        /**
         * 多标识登录查询（核心，支持账号/手机号/用户名任意匹配）
         * @param loginKey 登录标识（账号/手机号/用户名）
         * @return 有效商家信息（is_valid=1），无则返回null
         */
        TMerchant selectMerchantByLoginKey(String loginKey);

        /**
         * 商家注册（插入新商家数据，适配数据库唯一约束）
         * @param merchant 商家实体（含账号/手机号/密码等核心字段）
         * @return 受影响行数（1=成功，0=失败）
         */
        int insertMerchant(TMerchant merchant);

        /**
         * 校验商家是否存在（替代外键，确保merchantId有效）
         * @param id 商家ID
         * @return 1=存在，0=不存在
         */
        int countMerchantById(@Param("id") Long id);

        /**
         * 商家更新休假设置（核心：修改营业状态+休假时间）
         * @param merchantId 商家ID
         * @param isHoliday 是否休假：0-营业，1-休假
         * @param holidayStart 休假开始时间（可为null）
         * @param holidayEnd 休假结束时间（可为null）
         * @return 受影响行数
         */
        int updateHolidaySetting(@Param("merchantId") Long merchantId,
                                 @Param("isHoliday") Integer isHoliday,
                                 @Param("holidayStart") Date holidayStart,
                                 @Param("holidayEnd") Date holidayEnd);

        /**
         * 查询商家营业状态（仅返回核心字段，适配校验场景）
         * @param merchantId 商家ID
         * @return 商家实体（含isHoliday等营业状态字段）
         */
        TMerchant selectBusinessStatusById(@Param("merchantId") Long merchantId);
    }
