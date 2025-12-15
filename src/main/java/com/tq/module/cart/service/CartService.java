// CartService.java
package com.tq.module.cart.service;

import com.tq.module.cart.dto.CartVO;

import java.util.List;

public interface CartService {
    String addItem(Long userId, Long productId, int quantity);
    CartVO updateQuantity(Long userId, Long cartItemId, int quantity);
    void deleteItem(Long userId, Long cartItemId);
    void deleteItems(Long userId, List<Long> ids);
    CartVO getCart(Long userId);
}
