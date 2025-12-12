package com.tq.module.category.controller;

import com.tq.common.api.Result;
import com.tq.module.category.dto.CategoryTreeNode;
import com.tq.module.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端分类接口
 * 对应 /api/categories/**
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取商品分类树
     */
    @GetMapping("/tree")
    public Result<List<CategoryTreeNode>> tree() {
        List<CategoryTreeNode> tree = categoryService.listCategoryTree();
        return Result.ok(tree);
    }
}
