package com.canteen.communitycanteenosystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("t_user") // 绑定数据库表名（和你mapper中的表名保持一致）
public class TUser {

  @TableId(type = IdType.AUTO) // 主键自增
  private Long id;
  private String openid;
  private String nickname;
  private String avatar;
  private String signature;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

  // 1-有效 0-删除，逻辑标识
  private Integer  isValid;
}