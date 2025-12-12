package com.tq.module.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 编辑商品请求体。
 * 对应接口：PUT /api/admin/products/{id}
 * 字段均为可选，仅更新非 null 字段。
 */
@Data
public class ProductUpdateRequest {

    private String title;

    private String subTitle;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Long categoryId;

    private String mainImage;

    private List<String> imageList;

    /**
     * 商品状态：ON_SHELF / OFF_SHELF
     * 也可以使用专门的状态接口进行修改。
     */
    private String status;
}