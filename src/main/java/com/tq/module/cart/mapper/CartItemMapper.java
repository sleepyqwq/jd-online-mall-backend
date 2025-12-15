package com.tq.module.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tq.module.cart.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    /**
     * 绕过 MP 的逻辑删除过滤，查询数据库中真实存在的记录
     */
    @Select("SELECT id,user_id,product_id,quantity,create_time,update_time,deleted " +
            "FROM t_cart_item " +
            "WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1 FOR UPDATE")
    CartItem selectRawByUserIdAndProductId(@Param("userId") Long userId,
                                           @Param("productId") Long productId);

    /**
     * 强制恢复已删除的记录：将 deleted 置为 0，并更新数量和时间
     * 注意：这里不能用 MP 的 updateById，因为它会自动追加 AND deleted=0 导致更新失败
     */
    @Update("UPDATE t_cart_item " +
            "SET quantity = #{quantity}, deleted = 0, update_time = NOW() " +
            "WHERE id = #{id}")
    void recoverAndUpdateQuantity(@Param("id") Long id,
                                 @Param("quantity") Integer quantity);
}