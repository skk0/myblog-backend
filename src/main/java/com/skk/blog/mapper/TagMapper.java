package com.skk.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skk.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> selectTagsWithCount();
}
