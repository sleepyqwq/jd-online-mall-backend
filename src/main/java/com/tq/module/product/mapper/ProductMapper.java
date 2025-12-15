package com.tq.module.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tq.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品表 Mapper。
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Update("""
UPDATE t_product
SET stock = stock - #{qty}, update_time = NOW()
WHERE id = #{productId}
  AND deleted = 0
  AND status = 'ON_SHELF'
  AND stock >= #{qty}
""")
    int deductStock(@Param("productId") Long productId, @Param("qty") Integer qty);

    @Update("""
UPDATE t_product
SET stock = stock + #{qty}, update_time = NOW()
WHERE id = #{productId}
""")
    int rollbackStock(@Param("productId") Long productId, @Param("qty") Integer qty);


}
