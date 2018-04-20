package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.util.Page;

import java.util.List;

public interface CategoryService {
    List<Category> list();
    
    void add(Category category);

    void delete(Integer id);

    Category get(Integer id);

    void update(Category category);
}
