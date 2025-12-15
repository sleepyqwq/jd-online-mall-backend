package com.tq.module.cart.controller;

import com.tq.common.api.Result;
import com.tq.module.cart.dto.CartAddRequest;
import com.tq.module.cart.dto.CartBatchDeleteRequest; // 补充引入
import com.tq.module.cart.dto.CartUpdateRequest;     // 补充引入
import com.tq.module.cart.dto.CartVO;                // 补充引入
import com.tq.module.cart.service.CartService;
import com.tq.security.context.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
// 建议：可以统一加 @RequestMapping("/api/cart")，下面方法路径简写，这里先保持您原有的全路径风格
public class CartController {

    private final CartService cartService;

    public record CartAddResponse(String cartItemId) {}

    /**
     * 查询当前用户购物车
     * 对应前端：getCartList() -> GET /api/cart
     */
    @GetMapping("/api/cart")
    public Result<CartVO> list() {
        Long userId = UserContext.getUserId();
        CartVO cartVO = cartService.getCart(userId);
        return Result.ok(cartVO);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/api/cart/items")
    public Result<CartAddResponse> add(@RequestBody @Valid CartAddRequest request) {
        Long userId = UserContext.getUserId();
        String cartItemId = cartService.addItem(
                userId,
                Long.valueOf(request.getProductId()),
                request.getQuantity()
        );
        return Result.ok(new CartAddResponse(cartItemId));
    }

    /**
     * 修改购物车商品数量
     * 对应前端：updateCartItem -> PUT /api/cart/items/{cartItemId}
     */
    @PutMapping("/api/cart/items/{cartItemId}")
    public Result<CartVO> updateQuantity(@PathVariable("cartItemId") Long cartItemId,
                                         @RequestBody @Valid CartUpdateRequest request) {
        Long userId = UserContext.getUserId();
        // Service层已实现返回最新CartVO
        CartVO cartVO = cartService.updateQuantity(userId, cartItemId, request.getQuantity());
        return Result.ok(cartVO);
    }

    /**
     * 删除购物车单项
     * 对应前端：deleteCartItem -> DELETE /api/cart/items/{cartItemId}
     */
    @DeleteMapping("/api/cart/items/{cartItemId}")
    public Result<Void> deleteItem(@PathVariable("cartItemId") Long cartItemId) {
        Long userId = UserContext.getUserId();
        cartService.deleteItem(userId, cartItemId);
        return Result.ok();
    }

    /**
     * 批量删除购物车项
     * 对应前端：deleteCartItemsBatch -> DELETE /api/cart/items
     */
    @DeleteMapping("/api/cart/items")
    public Result<Void> deleteBatch(@RequestBody @Valid CartBatchDeleteRequest request) {
        Long userId = UserContext.getUserId();
        // 将 String 列表转为 Long 列表
        var ids = request.getIds().stream().map(Long::valueOf).toList();
        cartService.deleteItems(userId, ids);
        return Result.ok();
    }
}