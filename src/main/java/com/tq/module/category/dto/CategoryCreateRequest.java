package com.tq.module.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增分类请求
 */
@Data
public class CategoryCreateRequest {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 父分类 ID，0 或 null 表示一级分类
     */
    @NotNull(message = "parentId 不能为空")
    private Long parentId;

    /**
     * 排序值，越小越靠前
     */
    @NotNull(message = "排序值不能为空")
    private Integer sortOrder;
}
