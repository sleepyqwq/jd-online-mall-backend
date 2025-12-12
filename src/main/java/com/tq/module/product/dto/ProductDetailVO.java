package com.tq.module.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情 VO。
 * 对应接口：GET /api/products/{id}
 */
@Data
public class ProductDetailVO {

    private String id;

    private String title;

    private String subTitle;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String categoryId;

    /** 详情页图片列表，包含主图与其它图片 */
    private List<String> images;

    private LocalDateTime createTime;
}