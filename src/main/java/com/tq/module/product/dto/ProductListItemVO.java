package com.tq.module.product.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 前台商品列表项 VO。
 * 对应接口：GET /api/products
 */
@Data
public class ProductListItemVO {

    private String id;

    private String title;

    private String subTitle;

    private String mainImage;

    private BigDecimal price;

    private Integer stock;

    private String categoryId;
}