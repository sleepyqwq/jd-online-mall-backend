package com.tq.module.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增商品请求体。
 * 对应接口：POST /api/admin/products
 */
@Data
public class ProductCreateRequest {

    @NotBlank(message = "商品标题不能为空")
    private String title;

    private String subTitle;

    private String description;

    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /** 主图 URL，与文件上传接口返回的 fullUrl 对应 */
    @NotBlank(message = "主图不能为空")
    private String mainImage;

    /** 其它图片 URL 列表 */
    private List<String> imageList;

    /** 初始状态，若为空则默认为 ON_SHELF */
    private String status;
}
