package com.skk.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skk.blog.entity.BlogInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BlogInfoMapper extends BaseMapper<BlogInfo> {

    List<Map<String, String>> selectAllKeyValue();

    BlogInfo selectByKey(@Param("configKey") String configKey);
}
