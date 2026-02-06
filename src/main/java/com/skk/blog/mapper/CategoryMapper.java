package com.skk.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skk.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    List<Category> selectCategoriesWithCount();
}
