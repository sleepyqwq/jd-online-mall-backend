package com.tq.module.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 后台商品列表项 VO。
 * 对应接口：GET /api/admin/products
 */
@Data
public class AdminProductListItemVO {

    private String id;

    private String title;

    private String subTitle;

    private String mainImage;
    private String imageList; // 新增字段，用于前端编辑回显
    private String description;//详情描述

    private BigDecimal price;

    private Integer stock;

    private String categoryId;

    private String status;

    private LocalDateTime createTime;
}