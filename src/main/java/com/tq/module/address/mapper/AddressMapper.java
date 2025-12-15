package com.tq.module.address.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tq.module.address.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    @Update("UPDATE t_address SET is_default = 0, update_time = NOW() WHERE user_id = #{userId} AND deleted = 0")
    void clearDefault(@Param("userId") Long userId);

    @Update("UPDATE t_address SET is_default = 1, update_time = NOW() WHERE id = #{id} AND user_id = #{userId} AND deleted = 0")
    int setDefault(@Param("userId") Long userId, @Param("id") Long id);
}
