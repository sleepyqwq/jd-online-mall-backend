package com.tq.module.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应表 t_user
 */
@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    //登录账号，唯一
    private String username;

    //加密后的密码（BCrypt）
    private String password;

    //昵称
    private String nickname;

    //头像地址（建议存相对路径）
    private String avatar;

    //角色：USER / ADMIN
    private String role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
