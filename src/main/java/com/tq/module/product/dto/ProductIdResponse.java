package com.tq.module.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品创建返回体，仅包含新建 ID。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdResponse {

    private String id;
}
