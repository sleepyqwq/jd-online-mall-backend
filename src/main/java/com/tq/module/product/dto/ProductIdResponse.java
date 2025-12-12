package com.tq.module.product.dto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tq.module.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品创建返回体，仅包含新建 ID。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdResponse {

    private String id;
}
