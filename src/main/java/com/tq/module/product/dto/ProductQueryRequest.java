package com.tq.module.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品查询请求体（同时用于前台与后台列表查询）
 * 前台 /api/products 使用字段：categoryId、keyword、sortField、sortOrder、pageNum、pageSize。
 * 后台 /api/admin/products 使用字段：title、categoryId、status、pageNum、pageSize。
 */
@Data
public class ProductQueryRequest {

    /** 前台关键字搜索，按标题 / 副标题模糊匹配 */
    private String keyword;

    /** 分类 ID，可选 */
    private Long categoryId;

    /** 排序字段：price / createTime，仅前台使用 */
    private String sortField;

    /** 排序方式：asc / desc，仅前台使用 */
    private String sortOrder;

    /** 后台标题模糊匹配 */
    private String title;

    /** 后台商品状态过滤：ON_SHELF / OFF_SHELF */
    private String status;

    /** 页码，从 1 开始 */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于等于 1")
    private Integer pageNum;

    /** 每页数量 */
    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量必须大于等于 1")
    private Integer pageSize;
}