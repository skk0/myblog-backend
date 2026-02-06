package com.skk.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skk.blog.entity.Category;
import com.skk.blog.dto.CategoryDTO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 获取所有分类
     */
    List<Category> getAllCategories();

    /**
     * 获取分类列表(含文章数)
     */
    List<Category> getCategoriesWithCount();

    /**
     * 根据ID获取分类
     */
    Category getCategoryById(Long id);

    /**
     * 根据slug获取分类
     */
    Category getCategoryBySlug(String slug);

    /**
     * 创建分类
     */
    Long createCategory(CategoryDTO dto);

    /**
     * 更新分类
     */
    void updateCategory(Long id, CategoryDTO dto);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 更新排序
     */
    void updateSort(List<Long> ids);
}
