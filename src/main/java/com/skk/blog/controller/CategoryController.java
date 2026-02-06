package com.skk.blog.controller;

import com.skk.blog.common.Result;
import com.skk.blog.dto.CategoryDTO;
import com.skk.blog.entity.Category;
import com.skk.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@Tag(name = "分类管理")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "获取分类列表")
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/with-count")
    @Operation(summary = "获取分类列表(含文章数)")
    public Result<List<Category>> getCategoriesWithCount() {
        List<Category> categories = categoryService.getCategoriesWithCount();
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情")
    public Result<Category> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    @PostMapping
    @Operation(summary = "创建分类")
    public Result<Long> createCategory(@Valid @RequestBody CategoryDTO dto) {
        Long id = categoryService.createCategory(dto);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分类")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(id, dto);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/sort")
    @Operation(summary = "更新排序")
    public Result<Void> updateSort(@RequestBody List<Long> ids) {
        categoryService.updateSort(ids);
        return Result.success("排序更新成功", null);
    }
}
