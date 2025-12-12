package com.tq.module.category.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tq.module.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类表 Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
