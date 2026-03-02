package com.canteen.communitycanteenosystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.communitycanteenosystem.entity.TOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.mapper
 * @Author: 夏日花店
 * @CreateTime: 2026/02/07 19:44
 * @Description: 用户订单Mapper层
 * @Version: 2.0
 */
@Mapper
public interface TUserOrderMapper extends BaseMapper<TOrder> {
}
