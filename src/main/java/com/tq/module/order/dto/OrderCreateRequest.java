package com.tq.module.order.dto;

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

}
