package com.ecom.service;

import com.ecom.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    public Category saveCategory(Category category);

    public List<Category> getAllCategories();

    public Boolean existCategory(String name);

    public Boolean deleteCategory(int id);

    public Category getCategoryById(int id);

    public List<Category> getAllActiveCategory();

    public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize);
}
