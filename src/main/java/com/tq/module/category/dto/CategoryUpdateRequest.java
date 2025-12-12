package com.tq.module.category.dto;

import lombok.Data;

/**
 * 更新分类请求
 */
@Data
public class CategoryUpdateRequest {

    /**
     * 分类名称，可选
     */
    private String name;

    /**
     * 父分类 ID，可选
     */
    private Long parentId;

    /**
     * 排序值，可选
     */
    private Integer sortOrder;
}
