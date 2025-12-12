package com.tq.module.product.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体，对应表 t_product。
 * 说明：
 * - price 使用 BigDecimal 表示金额；
 * - imageList 存储为逗号分隔的图片 URL 列表；
 * - status 取值约定：ON_SHELF / OFF_SHELF。
 */
@Data
@TableName("t_product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品标题 */
    private String title;

    /** 副标题 */
    private String subTitle;

    /** 商品详情描述 */
    private String description;

    /** 商品价格，单位元 */
    private BigDecimal price;

    /** 库存数量 */
    private Integer stock;

    /** 分类 ID */
    private Long categoryId;

    /** 商品主图 */
    private String mainImage;

    /** 其它图片 URL，逗号分隔存储 */
    private String imageList;

    /** 商品状态：ON_SHELF / OFF_SHELF */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}