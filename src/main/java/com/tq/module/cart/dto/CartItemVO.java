// CartItemVO.java
package com.tq.module.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {
    private String cartItemId;
    private String productId;
    private String productTitle;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotalAmount;
}
