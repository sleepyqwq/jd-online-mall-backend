package com.tq.module.category.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类树节点 DTO，ID 统一使用字符串，满足接口文档要求
 */
@Data
public class CategoryTreeNode {

    private String id;

    private String name;

    /**
     * 父分类 ID，"0" 表示顶级
     */
    private String parentId;

    /**
     * 排序值
     */
    private Integer sortOrder;

    /**
     * 子节点列表，二级分类 children 为空列表
     */
    private List<CategoryTreeNode> children = new ArrayList<>();
}
