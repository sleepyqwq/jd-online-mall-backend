// CartServiceImpl.java（核心逻辑示意）
package com.tq.module.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tq.common.api.ErrorCode;
import com.tq.common.exception.BusinessException;
import com.tq.module.cart.dto.CartItemVO;
import com.tq.module.cart.dto.CartVO;
import com.tq.module.cart.entity.CartItem;
import com.tq.module.cart.mapper.CartItemMapper;
import com.tq.module.cart.service.CartService;
import com.tq.module.product.entity.Product;
import com.tq.module.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public String addItem(Long userId, Long productId, int quantity) {
        Product p = getOnShelfProductOrThrow(productId);


        if (quantity > p.getStock()) {
            throw stockNotEnough(p.getStock());
        }


        CartItem exist = cartItemMapper.selectRawByUserIdAndProductId(userId, productId);


        if (exist == null) {
            CartItem ci = new CartItem();
            ci.setUserId(userId);
            ci.setProductId(productId);
            ci.setQuantity(quantity);
            ci.setCreateTime(LocalDateTime.now());
            ci.setUpdateTime(LocalDateTime.now());
            ci.setDeleted(0);
            cartItemMapper.insert(ci);
            return String.valueOf(ci.getId());
        }


        boolean wasDeleted = exist.getDeleted() != null && exist.getDeleted() == 1;
        int baseQty = wasDeleted ? 0 : exist.getQuantity();
        int newQty = baseQty + quantity;


        if (newQty > p.getStock()) {
            throw stockNotEnough(p.getStock());
        }


        if (wasDeleted) {
            cartItemMapper.recoverAndUpdateQuantity(exist.getId(), newQty);
        } else {
            exist.setQuantity(newQty);
            exist.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(exist);
        }


        return String.valueOf(exist.getId());
    }

    @Override
    @Transactional
    public CartVO updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem ci = getUserCartItemOrThrow(userId, cartItemId);
        Product p = getOnShelfProductOrThrow(ci.getProductId());

        if (quantity > p.getStock()) {
            throw stockNotEnough(p.getStock());
        }

        ci.setQuantity(quantity);
        ci.setUpdateTime(LocalDateTime.now());
        cartItemMapper.updateById(ci);

        // 接口要求：修改成功后返回最新购物车汇总信息
        return getCart(userId);
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long cartItemId) {
        CartItem ci = getUserCartItemOrThrow(userId, cartItemId);
        cartItemMapper.deleteById(ci.getId());
    }

    @Override
    @Transactional
    public void deleteItems(Long userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 只删除当前用户自己的条目
        LambdaQueryWrapper<CartItem> qw = new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, ids);
        cartItemMapper.delete(qw);
    }

    @Override
    public CartVO getCart(Long userId) {
        List<CartItem> items = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId)
        );

        if (items.isEmpty()) {
            CartVO vo = new CartVO();
            vo.setItems(Collections.emptyList());
            vo.setTotalQuantity(0);
            vo.setTotalAmount(BigDecimal.ZERO);
            return vo;
        }

        List<Long> productIds = items.stream().map(CartItem::getProductId).distinct().toList();
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> pMap = products.stream().collect(Collectors.toMap(Product::getId, x -> x, (a, b) -> a));

        List<CartItemVO> voItems = new ArrayList<>();
        int totalQty = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem ci : items) {
            Product p = pMap.get(ci.getProductId());
            if (p == null) {
                continue; // 商品被删的极端情况：先跳过，后续可按需要清理无效条目
            }

            BigDecimal price = p.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(ci.getQuantity()));

            CartItemVO ivo = new CartItemVO();
            ivo.setCartItemId(String.valueOf(ci.getId()));
            ivo.setProductId(String.valueOf(ci.getProductId()));
            ivo.setProductTitle(p.getTitle());
            ivo.setProductImage(p.getMainImage()); // 建议此处已经是完整URL（沿用你现有图片返回策略）
            ivo.setPrice(price);
            ivo.setQuantity(ci.getQuantity());
            ivo.setSubtotalAmount(subtotal);

            voItems.add(ivo);
            totalQty += ci.getQuantity();
            totalAmount = totalAmount.add(subtotal);
        }

        CartVO vo = new CartVO();
        vo.setItems(voItems);
        vo.setTotalQuantity(totalQty);
        vo.setTotalAmount(totalAmount);
        return vo;
    }

    private CartItem getUserCartItemOrThrow(Long userId, Long cartItemId) {
        CartItem ci = cartItemMapper.selectById(cartItemId);
        if (ci == null || !Objects.equals(ci.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "购物车记录不存在");
        }
        return ci;
    }

    private Product getOnShelfProductOrThrow(Long productId) {
        Product p = productMapper.selectById(productId);
        if (p == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        // 建议：下架商品不允许加入或修改数量（避免结算时才失败）
        if (!"ON_SHELF".equals(p.getStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品已下架");
        }
        return p;
    }

    private BusinessException stockNotEnough(int availableStock) {
        return new BusinessException(
                ErrorCode.STOCK_NOT_ENOUGH,
                ErrorCode.STOCK_NOT_ENOUGH.getMessage(),
                Map.of("availableStock", availableStock)
        );
    }
}
