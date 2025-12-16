package com.tq.module.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_banner")
public class Banner {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String imgUrl;
    private String redirectUrl;
    private Integer sortOrder;
    private Integer status; // 1:启用 0:禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}