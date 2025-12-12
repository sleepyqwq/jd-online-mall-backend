package com.tq.module.product.controller;

import com.tq.common.api.PageResult;
import com.tq.common.api.Result;
import com.tq.module.product.dto.ProductDetailVO;
import com.tq.module.product.dto.ProductListItemVO;
import com.tq.module.product.dto.ProductQueryRequest;
import com.tq.module.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品浏览与搜索接口控制器。
 * 对应接口文档中的 /api/products 相关接口。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 分页查询商品列表。
     * GET /api/products
     */
    @GetMapping
    public Result<PageResult<ProductListItemVO>> pageProducts(@Valid ProductQueryRequest request) {
        PageResult<ProductListItemVO> page = productService.pageProducts(request);
        return Result.ok(page);
    }

    /**
     * 查询商品详情。
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductDetail(@PathVariable("id") Long id) {
        ProductDetailVO detail = productService.getProductDetail(id);
        return Result.ok(detail);
    }
}