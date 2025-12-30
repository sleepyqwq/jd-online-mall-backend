package com.tq.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tq.common.api.ErrorCode;
import com.tq.common.api.PageResult;
import com.tq.common.exception.BusinessException;
import com.tq.common.exception.NotFoundException;
import com.tq.module.category.entity.Category;
import com.tq.module.category.mapper.CategoryMapper;
import com.tq.module.product.dto.AdminProductListItemVO;
import com.tq.module.product.dto.ProductCreateRequest;
import com.tq.module.product.dto.ProductDetailVO;
import com.tq.module.product.dto.ProductListItemVO;
import com.tq.module.product.dto.ProductQueryRequest;
import com.tq.module.product.dto.ProductUpdateRequest;
import com.tq.module.product.entity.Product;
import com.tq.module.product.mapper.ProductMapper;
import com.tq.module.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String STATUS_ON_SHELF = "ON_SHELF";
    private static final String STATUS_OFF_SHELF = "OFF_SHELF";

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<ProductListItemVO> pageProducts(ProductQueryRequest request) {
        PageParam pp = requirePageParam(request);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 仅查询未删除且已上架商品
        wrapper.eq(Product::getStatus, STATUS_ON_SHELF);

        // 分类筛选：支持一级分类聚合查询
        if (request.getCategoryId() != null) {
            Long categoryId = request.getCategoryId();
            applyCategoryFilter(wrapper, categoryId);
        }

        // 关键字搜索：按标题、副标题模糊匹配
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            wrapper.and(w -> w.like(Product::getTitle, keyword)
                    .or()
                    .like(Product::getSubTitle, keyword));
        }

        // 排序
        boolean asc = "asc".equalsIgnoreCase(request.getSortOrder());
        String sortField = request.getSortField();
        if ("price".equals(sortField)) {
            wrapper.orderBy(true, asc, Product::getPrice);
        } else if ("createTime".equals(sortField)) {
            wrapper.orderBy(true, asc, Product::getCreateTime);
        } else {
            // 默认按上架时间倒序
            wrapper.orderByDesc(Product::getCreateTime);
        }

        Page<Product> page = new Page<>(pp.pageNum(), pp.pageSize());
        Page<Product> resultPage = productMapper.selectPage(page, wrapper);

        List<ProductListItemVO> list = resultPage.getRecords().stream()
                .map(this::toProductListItemVO)
                .collect(Collectors.toList());

        return new PageResult<>(
                resultPage.getTotal(),
                list,
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        // 尝试从缓存中读取
        String cacheKey = "cache:product_detail:" + id;
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cacheValue)) {
            try {
                ProductDetailVO cached = objectMapper.readValue(cacheValue, ProductDetailVO.class);
                if (cached != null) {
                    return cached;
                }
            } catch (Exception ignored) {
            }
        }

        Product product = productMapper.selectById(id);
        if (product == null || isDeleted(product)) {
            throw new NotFoundException("商品不存在");
        }
        // 前台商品详情仅允许查看已上架商品
        if (!STATUS_ON_SHELF.equals(product.getStatus())) {
            throw new NotFoundException("商品不存在");
        }

        ProductDetailVO vo = toProductDetailVO(product);

        // 写入缓存，TTL 30 分钟
        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(vo),
                    Duration.ofMinutes(30)
            );
        } catch (Exception ignored) {
        }

        return vo;
    }

    @Override
    public PageResult<AdminProductListItemVO> pageAdminProducts(ProductQueryRequest request) {
        PageParam pp = requirePageParam(request);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getTitle())) {
            String title = request.getTitle().trim();
            wrapper.like(Product::getTitle, title);
        }

        if (request.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, request.getCategoryId());
        }

        if (StringUtils.hasText(request.getStatus())) {
            String status = normalizeStatus(request.getStatus());
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> page = new Page<>(pp.pageNum(), pp.pageSize());
        Page<Product> resultPage = productMapper.selectPage(page, wrapper);

        List<AdminProductListItemVO> list = resultPage.getRecords().stream()
                .map(this::toAdminProductListItemVO)
                .collect(Collectors.toList());

        return new PageResult<>(
                resultPage.getTotal(),
                list,
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    @Transactional
    public Long createProduct(ProductCreateRequest request) {
        validatePriceAndStock(request.getPrice(), request.getStock());
        ensureCategoryExists(request.getCategoryId());

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setSubTitle(request.getSubTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(request.getCategoryId());
        product.setMainImage(request.getMainImage());
        product.setImageList(joinImageList(request.getImageList()));

        String status = request.getStatus();
        if (!StringUtils.hasText(status)) {
            status = STATUS_ON_SHELF;
        } else {
            status = normalizeStatus(status);
        }
        product.setStatus(status);

        LocalDateTime now = LocalDateTime.now();
        product.setCreateTime(now);
        product.setUpdateTime(now);

        int rows = productMapper.insert(product);
        if (rows != 1 || product.getId() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "新增商品失败");
        }
        return product.getId();
    }

    @Override
    @Transactional
    public void updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productMapper.selectById(id);
        if (product == null || isDeleted(product)) {
            throw new NotFoundException("商品不存在");
        }

        if (StringUtils.hasText(request.getTitle())) {
            product.setTitle(request.getTitle().trim());
        }
        if (request.getSubTitle() != null) {
            product.setSubTitle(request.getSubTitle());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            validatePriceAndStock(request.getPrice(), product.getStock());
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            validatePriceAndStock(product.getPrice(), request.getStock());
            product.setStock(request.getStock());
        }
        if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategoryId())) {
            ensureCategoryExists(request.getCategoryId());
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getMainImage() != null) {
            product.setMainImage(request.getMainImage());
        }
        if (request.getImageList() != null) {
            product.setImageList(joinImageList(request.getImageList()));
        }
        if (StringUtils.hasText(request.getStatus())) {
            product.setStatus(normalizeStatus(request.getStatus()));
        }

        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        // 删除缓存
        stringRedisTemplate.delete("cache:product_detail:" + id);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long id, String status) {
        Product product = productMapper.selectById(id);
        if (product == null || isDeleted(product)) {
            throw new NotFoundException("商品不存在");
        }
        product.setStatus(normalizeStatus(status));
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        // 删除缓存
        stringRedisTemplate.delete("cache:product_detail:" + id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || isDeleted(product)) {
            throw new NotFoundException("商品不存在");
        }

        // TODO：商品与订单存在关联时，可在订单模块实现“有未完成订单的商品禁止删除”的额外校验。
        productMapper.deleteById(id);

        // 删除缓存
        stringRedisTemplate.delete("cache:product_detail:" + id);
    }

    /**
     * 分类筛选：
     * - 若为二级分类，则直接按该分类 ID 查询；
     * - 若为一级分类，则包含该一级分类及其所有子分类。
     */
    private void applyCategoryFilter(LambdaQueryWrapper<Product> wrapper, Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || (category.getDeleted() != null && category.getDeleted() == 1)) {
            // 分类不存在时，返回空结果集
            wrapper.eq(Product::getCategoryId, -1L);
            return;
        }

        Long parentId = category.getParentId();
        if (parentId == null || parentId == 0L) {
            // 一级分类：查询其本身 + 所有子分类
            List<Category> children = categoryMapper.selectList(
                    new LambdaQueryWrapper<Category>()
                            .eq(Category::getParentId, categoryId)
            );
            Set<Long> ids = children.stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            ids.add(categoryId);
            wrapper.in(Product::getCategoryId, ids);
        } else {
            // 二级分类：仅按自身分类 ID 查询
            wrapper.eq(Product::getCategoryId, categoryId);
        }
    }

    private boolean isDeleted(Product product) {
        return product.getDeleted() != null && product.getDeleted() == 1;
    }

    private void validatePriceAndStock(BigDecimal price, Integer stock) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "商品价格不能为负");
        }
        if (stock != null && stock < 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "库存不能为负");
        }
    }

    private void ensureCategoryExists(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类不能为空");
        }
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || (category.getDeleted() != null && category.getDeleted() == 1)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "所属分类不存在");
        }
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "商品状态不能为空");
        }
        String upper = status.trim().toUpperCase();
        if (STATUS_ON_SHELF.equals(upper) || STATUS_OFF_SHELF.equals(upper)) {
            return upper;
        }
        throw new BusinessException(ErrorCode.PARAM_INVALID, "商品状态不合法");
    }

    private String joinImageList(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            return null;
        }
        List<String> filtered = imageList.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toList());
        if (filtered.isEmpty()) {
            return null;
        }
        return String.join(",", filtered);
    }

    private List<String> parseImageList(String imageList) {
        if (!StringUtils.hasText(imageList)) {
            return Collections.emptyList();
        }
        String[] arr = imageList.split(",");
        List<String> result = new ArrayList<>();
        Arrays.stream(arr)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .forEach(result::add);
        return result;
    }

    private ProductListItemVO toProductListItemVO(Product product) {
        ProductListItemVO vo = new ProductListItemVO();
        fillListCommonFields(
                product,
                vo::setId,
                vo::setTitle,
                vo::setSubTitle,
                vo::setMainImage,
                vo::setPrice,
                vo::setStock,
                vo::setCategoryId
        );
        return vo;
    }

    private AdminProductListItemVO toAdminProductListItemVO(Product product) {
        AdminProductListItemVO vo = new AdminProductListItemVO();
        fillListCommonFields(
                product,
                vo::setId,
                vo::setTitle,
                vo::setSubTitle,
                vo::setMainImage,
                vo::setPrice,
                vo::setStock,
                vo::setCategoryId
        );

        vo.setStatus(product.getStatus());
        vo.setCreateTime(product.getCreateTime());
        vo.setImageList(product.getImageList());
        vo.setDescription(product.getDescription());
        return vo;
    }

    private ProductDetailVO toProductDetailVO(Product product) {
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(String.valueOf(product.getId()));
        vo.setTitle(product.getTitle());
        vo.setSubTitle(product.getSubTitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setStock(product.getStock());
        vo.setCategoryId(String.valueOf(product.getCategoryId()));

        List<String> images = parseImageList(product.getImageList());
        if (images.isEmpty() && StringUtils.hasText(product.getMainImage())) {
            images = new ArrayList<>();
            images.add(product.getMainImage());
        }
        vo.setImages(images);
        vo.setCreateTime(product.getCreateTime());
        return vo;
    }

    private record PageParam(int pageNum, int pageSize) {
    }

    private PageParam requirePageParam(ProductQueryRequest request) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        if (pageNum == null || pageSize == null || pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分页参数不合法");
        }
        return new PageParam(pageNum, pageSize);
    }

    private void fillListCommonFields(
            Product product,
            Consumer<String> setId,
            Consumer<String> setTitle,
            Consumer<String> setSubTitle,
            Consumer<String> setMainImage,
            Consumer<BigDecimal> setPrice,
            Consumer<Integer> setStock,
            Consumer<String> setCategoryId
    ) {
        setId.accept(String.valueOf(product.getId()));
        setTitle.accept(product.getTitle());
        setSubTitle.accept(product.getSubTitle());
        setMainImage.accept(product.getMainImage());
        setPrice.accept(product.getPrice());
        setStock.accept(product.getStock());
        setCategoryId.accept(String.valueOf(product.getCategoryId()));
    }
}