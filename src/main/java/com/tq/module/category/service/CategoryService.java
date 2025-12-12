package com.tq.module.category.service;

import com.tq.module.category.dto.CategoryCreateRequest;
import com.tq.module.category.dto.CategoryTreeNode;
import com.tq.module.category.dto.CategoryUpdateRequest;

import java.util.List;

/**
 * 分类业务接口
 */
public interface CategoryService {

    /**
     * 查询分类树结构，供前台与后台复用。
     * 约定仅两级分类。
     */
    List<CategoryTreeNode> listCategoryTree();

    /** 新增分类 */
    void createCategory(CategoryCreateRequest request);

    /** 编辑分类 */
    void updateCategory(Long id, CategoryUpdateRequest request);

    /** 删除分类 */
    void deleteCategory(Long id);
}
