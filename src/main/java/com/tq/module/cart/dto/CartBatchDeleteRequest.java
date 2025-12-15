// CartBatchDeleteRequest.java
package com.tq.module.cart.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CartBatchDeleteRequest {
    @NotEmpty(message = "ids不能为空")
    private List<String> ids;
}
