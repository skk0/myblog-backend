package com.skk.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skk.blog.dto.CategoryDTO;
import com.skk.blog.entity.Category;
import com.skk.blog.mapper.CategoryMapper;
import com.skk.blog.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> getAllCategories() {
        return this.lambdaQuery()
                .orderByAsc(Category::getSortOrder)
                .list();
    }

    @Override
    public List<Category> getCategoriesWithCount() {
        return categoryMapper.selectCategoriesWithCount();
    }

    @Override
    public Category getCategoryById(Long id) {
        return this.getById(id);
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        return this.lambdaQuery()
                .eq(Category::getSlug, slug)
                .one();
    }

    @Override
    @Transactional
    public Long createCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setSlug(dto.getSlug() != null ? dto.getSlug() : generateSlug(dto.getName()));
        category.setIcon(dto.getIcon());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        this.save(category);
        return category.getId();
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryDTO dto) {
        Category category = this.getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        category.setName(dto.getName());
        if (dto.getSlug() != null) {
            category.setSlug(dto.getSlug());
        }
        category.setIcon(dto.getIcon());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        if (dto.getSortOrder() != null) {
            category.setSortOrder(dto.getSortOrder());
        }

        this.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateSort(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            Category category = this.getById(ids.get(i));
            if (category != null) {
                category.setSortOrder(i);
                this.updateById(category);
            }
        }
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
