package com.tq.module.product.service;

import com.tq.common.api.PageResult;
import com.tq.module.product.dto.AdminProductListItemVO;
import com.tq.module.product.dto.ProductCreateRequest;
import com.tq.module.product.dto.ProductDetailVO;
import com.tq.module.product.dto.ProductListItemVO;
import com.tq.module.product.dto.ProductQueryRequest;
import com.tq.module.product.dto.ProductUpdateRequest;

/**
 * 商品领域服务接口。
 */
public interface ProductService {

    /**
     * 前台分页查询商品列表。
     */
    PageResult<ProductListItemVO> pageProducts(ProductQueryRequest request);

    /**
     * 查询商品详情（前台）。
     */
    ProductDetailVO getProductDetail(Long id);

    /**
     * 后台分页查询商品列表。
     */
    PageResult<AdminProductListItemVO> pageAdminProducts(ProductQueryRequest request);

    /** 新增商品，返回新建商品 ID */
    Long createProduct(ProductCreateRequest request);

    /** 编辑商品 */
    void updateProduct(Long id, ProductUpdateRequest request);

    /** 修改商品上下架状态 */
    void updateProductStatus(Long id, String status);

    /** 删除商品 */
    void deleteProduct(Long id);
}