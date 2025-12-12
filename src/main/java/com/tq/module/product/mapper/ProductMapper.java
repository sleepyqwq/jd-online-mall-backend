package com.tq.module.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tq.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表 Mapper。
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}