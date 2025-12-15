package com.tq.module.order.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    @NotBlank(message = "sourceType不能为空")
    private String sourceType; // CART / BUY_NOW

    private List<String> cartItemIds; // sourceType=CART 时必填

    private String productId; // sourceType=BUY_NOW 时必填
    private Integer quantity; // sourceType=BUY_NOW 时必填

    @NotBlank(message = "addressId不能为空")
    private String addressId;

    private String remark;

    @AssertTrue(message = "sourceType=CART 时必须传 cartItemIds；sourceType=BUY_NOW 时必须传 productId 和 quantity")
    public boolean isValidBySourceType() {
        if ("CART".equals(sourceType)) {
            return cartItemIds != null && !cartItemIds.isEmpty();
        }
        if ("BUY_NOW".equals(sourceType)) {
            return productId != null && !productId.isBlank() && quantity != null && quantity > 0;
        }
        return false;
    }
}
