package com.tq.module.product.controller;

import com.tq.common.api.PageResult;
import com.tq.common.api.Result;
import com.tq.module.product.dto.AdminProductListItemVO;
import com.tq.module.product.dto.ProductCreateRequest;
import com.tq.module.product.dto.ProductIdResponse;
import com.tq.module.product.dto.ProductQueryRequest;
import com.tq.module.product.dto.ProductUpdateRequest;
import com.tq.module.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 后台商品管理控制器。
 * 对应接口文档中的商品管理模块。
 */
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 后台分页查询商品列表。
     * GET /api/admin/products
     */
    @GetMapping
    public Result<PageResult<AdminProductListItemVO>> pageProducts(@Valid ProductQueryRequest request) {
        PageResult<AdminProductListItemVO> page = productService.pageAdminProducts(request);
        return Result.ok(page);
    }

    /**
     * 新增商品。
     * POST /api/admin/products
     */
    @PostMapping
    public Result<ProductIdResponse> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        Long id = productService.createProduct(request);
        return Result.ok(new ProductIdResponse(String.valueOf(id)));
    }

    /**
     * 编辑商品。
     * PUT /api/admin/products/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable("id") Long id,
                                      @RequestBody ProductUpdateRequest request) {
        productService.updateProduct(id, request);
        return Result.ok();
    }

    /**
     * 上架 / 下架商品。
     * PUT /api/admin/products/{id}/status
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id,
                                     @RequestBody ProductUpdateRequest request) {
        productService.updateProductStatus(id, request.getStatus());
        return Result.ok();
    }

    /**
     * 删除商品。
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return Result.ok();
    }
}
