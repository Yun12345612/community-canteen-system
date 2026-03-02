package com.canteen.communitycanteenosystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.communitycanteenosystem.entity.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
 * @Author: 夏日花店
 * @CreateTime: 2026/1/31 23:14
 * @Description: 用户Mapper
 * @Version: 1.0
 */
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    TUser selectById(@Param("id") Long id);

    /**
     * 根据OpenID查询用户
     */
    @Select("SELECT * FROM t_user WHERE openid = #{openid} AND isValid = 1")
    TUser selectByOpenId(@Param("openid") String openid);
}
