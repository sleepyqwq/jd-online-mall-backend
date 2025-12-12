package com.tq.module.category.controller;

import com.tq.common.api.Result;
import com.tq.module.category.dto.CategoryCreateRequest;
import com.tq.module.category.dto.CategoryTreeNode;
import com.tq.module.category.dto.CategoryUpdateRequest;
import com.tq.module.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台分类管理接口
 * 对应 /api/admin/categories/**
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类树
     */
    @GetMapping
    public Result<List<CategoryTreeNode>> list() {
        List<CategoryTreeNode> tree = categoryService.listCategoryTree();
        return Result.ok(tree);
    }


    /**
     * 新增分类
     */
    @PostMapping
    public Result<Void> create(@RequestBody @Valid CategoryCreateRequest request) {
        categoryService.createCategory(request);
        // 接口文档未要求返回新建 ID，这里保持 data 为 null 的成功响应。
        return Result.ok();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable("id") Long id,
                               @RequestBody @Valid CategoryUpdateRequest request) {
        categoryService.updateCategory(id, request);
        return Result.ok();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return Result.ok();
    }
}
