// CartUpdateRequest.java
package com.tq.module.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartUpdateRequest {
    @Min(value = 1, message = "数量必须大于等于1")
    private Integer quantity;
}
